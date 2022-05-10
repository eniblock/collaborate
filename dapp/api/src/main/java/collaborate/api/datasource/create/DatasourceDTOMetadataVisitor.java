package collaborate.api.datasource.create;

import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_PURPOSE;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_TYPE;
import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;

import collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.Attribute;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatasourceDTOMetadataVisitor implements DatasourceDTOVisitor<Stream<Metadata>> {

  private static final String METADATA_PREFIX = "metadata:";

  @NoArgsConstructor(access = PRIVATE)
  public static final class Keys {

    public static final String DATASOURCE_PURPOSE = "datasource:purpose";
    public static final String DATASOURCE_TYPE = "datasource:type";
  }


  private final ObjectMapper objectMapper;

  @Override
  public Stream<Metadata> visitWebServerDatasource(
      WebServerDatasourceDTO webServerDatasourceDTO) throws DatasourceVisitorException {
    return Stream.concat(
        Stream.of(
            buildPurpose(webServerDatasourceDTO.getKeywords()),
            buildType(webServerDatasourceDTO)
        ),
        buildResources(webServerDatasourceDTO.getResources())
    );
  }

  Metadata buildType(DatasourceDTO datasourceDTO) {
    return Metadata.builder()
        .name(DATASOURCE_TYPE)
        .value(datasourceDTO.getType())
        .type("string")
        .build();
  }

  private Metadata buildPurpose(Set<String> keywords)
      throws DatasourceVisitorException {
    try {
      return new Metadata(
          DATASOURCE_PURPOSE,
          objectMapper.writeValueAsString(keywords),
          "string[]");
    } catch (JsonProcessingException e) {
      log.error("While generating metadata for datasource keywords={}", keywords);
      throw new DatasourceVisitorException(e);
    }
  }

  private Stream<Metadata> buildResources(List<WebServerResource> webServerResources) {
    var keywordMetadata = webServerResources.stream()
        .map(WebServerResource::getKeywords)
        .flatMap(this::buildResourceKeywords);
    var resources = webServerResources.stream()
        .map(r -> r.findFirstKeywordValueByName(ATTR_NAME_ALIAS))
        .flatMap(Optional::stream)
        .collect(Collectors.joining(","));
    var resourcesMetadata = Metadata.builder()
        .name("resources")
        .value(resources)
        .build();
    return Stream.concat(keywordMetadata, Stream.of(resourcesMetadata));
  }

  Stream<Metadata> buildResourceKeywords(Collection<Attribute> keywords) {
    var routingKey = new RoutingKeyFromKeywordSupplier(keywords).get();
    return keywords.stream()
        .filter(attr -> startsWith(attr.getName(), METADATA_PREFIX))
        .map(attr -> buildResourceMetadata(attr, routingKey));
  }


  private Metadata buildResourceMetadata(Attribute attribute, String routingKey) {
    return Metadata.builder()
        .name(routingKey + ":" + removeStart(attribute.getName(), METADATA_PREFIX))
        .value(attribute.getValue())
        .type(attribute.getType())
        .build();
  }
}
