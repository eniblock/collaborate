package collaborate.api.datasource.create;

import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_PURPOSE;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_TYPE;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.METADATA_REGEXP;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.TYPE_GROUP_INDEX;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.VALUE_GROUP_INDEX;
import static lombok.AccessLevel.PRIVATE;

import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.traefik.routing.RoutingKeyFromKeywordSupplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DatasourceDTOMetadataVisitor implements DatasourceDTOVisitor<Stream<Attribute>> {

  @NoArgsConstructor(access = PRIVATE)
  public static final class Regexp {

    private static final String METADATA_PREFIX = "metadata:";
    private static final String KEY_GROUP_REGEXP = "([^:]+)";
    private static final String VALUE_GROUP_REGEXP = ":([^:]*)";
    private static final String OPTIONAL_TYPE_GROUP_REGEXP = "(?::([A-z]*))?";

    public static final Pattern METADATA_REGEXP =
        Pattern.compile(
            METADATA_PREFIX + KEY_GROUP_REGEXP + VALUE_GROUP_REGEXP + OPTIONAL_TYPE_GROUP_REGEXP);
    public static final int NAME_GROUP_INDEX = 1;
    public static final int VALUE_GROUP_INDEX = 2;
    public static final int TYPE_GROUP_INDEX = 3;
  }

  @NoArgsConstructor(access = PRIVATE)
  public static final class Keys {

    public static final String DATASOURCE_PURPOSE = "datasource:purpose";
    public static final String DATASOURCE_TYPE = "datasource:type";
  }


  private final ObjectMapper objectMapper;

  @Override
  public Stream<Attribute> visitWebServerDatasource(
      WebServerDatasourceDTO webServerDatasourceDTO) throws DatasourceVisitorException {
    return Stream.concat(
        Stream.of(
            buildPurpose(webServerDatasourceDTO.getKeywords()),
            buildType(webServerDatasourceDTO)
        ),
        buildResources(webServerDatasourceDTO.getResources())
    );
  }

  Attribute buildType(DatasourceDTO datasourceDTO) {
    return Attribute.builder()
        .name(DATASOURCE_TYPE)
        .value(datasourceDTO.getClass().getSimpleName())
        .type("string")
        .build();
  }

  private Attribute buildPurpose(Set<String> keywords)
      throws DatasourceVisitorException {
    try {
      return new Attribute(
          DATASOURCE_PURPOSE,
          objectMapper.writeValueAsString(keywords),
          "string[]");
    } catch (JsonProcessingException e) {
      log.error("While generation metadata for datasource keywords={}", keywords);
      throw new DatasourceVisitorException(e);
    }
  }

  private Stream<Attribute> buildResources(List<WebServerResource> webServerResources) {
    return webServerResources.stream()
        .map(WebServerResource::getKeywords)
        .flatMap(this::buildResourceKeywords);
  }

  Stream<Attribute> buildResourceKeywords(Collection<String> keywords) {
    var routingKey = new RoutingKeyFromKeywordSupplier(keywords).get();
    return keywords.stream()
        .map(METADATA_REGEXP::matcher)
        .filter(Matcher::find)
        .map(matcher -> buildResourceKeyword(matcher, routingKey));
  }


  private Attribute buildResourceKeyword(Matcher matcher, String routingKey) {
    return Attribute.builder()
        .name(routingKey + ":" + matcher.group(Regexp.NAME_GROUP_INDEX))
        .value(matcher.group(VALUE_GROUP_INDEX))
        .type(matcher.group(TYPE_GROUP_INDEX))
        .build();
  }
}
