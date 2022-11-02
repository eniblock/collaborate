package collaborate.api.datasource.servicedata.model;

import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_PURPOSE;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_TYPE;
import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;

import collaborate.api.datasource.servicedata.model.ServiceDataDTO;

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
public class ServiceDataDTOMetadataVisitor implements ServiceDataDTOVisitor<Stream<Metadata>> {

  private static final String METADATA_PREFIX = "metadata:";

  @NoArgsConstructor(access = PRIVATE)
  public static final class Keys {

    public static final String DATASOURCE_PURPOSE = "datasource:purpose";
    public static final String DATASOURCE_TYPE = "datasource:type";
  }

  private final ObjectMapper objectMapper;

  @Override
  public Stream<Metadata> visit(ServiceDataDTO serviceDataDTO)  {
    return Stream.of(
            buildPurpose(serviceDataDTO.getServices())
        );
  }

  private Metadata buildPurpose(List<ServiceDataDTOElement> services)  {
    var resources = services.stream()
        .map(r -> { return r.getDatasource().toString()  + ":" + r.getScope(); })
        .collect(Collectors.joining(","));
    return Metadata.builder()
        .name("business-data")
        .value(resources)
        .type("string")
        .build();
  }

}
