package collaborate.api.datasource.create;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import collaborate.api.datasource.businessdata.AssetListService;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceEnrichment;
import collaborate.api.datasource.model.dto.DatasourcePurpose;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatasourceEnricherVisitor implements
    DatasourceDTOVisitor<DatasourceEnrichment<? extends DatasourceDTO>> {

  public static final String KEY_PATH = "$.id";
  private final AssetListService assetListService;

  @Override
  public DatasourceEnrichment<? extends DatasourceDTO> visitWebServerDatasource(
      WebServerDatasourceDTO webServerDatasourceDTO) {

    if (DatasourcePurpose.BUSINESS_DATA.match(webServerDatasourceDTO)) {
      return enrichBusinessData(webServerDatasourceDTO);
    }
    return buildIdentity(webServerDatasourceDTO);
  }

  private DatasourceEnrichment<WebServerDatasourceDTO> enrichBusinessData(
      WebServerDatasourceDTO webServerDatasourceDTO) {
    var response = assetListService.getAssetListResponse(webServerDatasourceDTO);
    return enrich(webServerDatasourceDTO, response);
  }


  DatasourceEnrichment<WebServerDatasourceDTO> enrich(WebServerDatasourceDTO datasource,
      String jsonResponse) {
    var resourcesPath = JSONPath.compile("$._embedded.metadatas");
    if (resourcesPath.contains(jsonResponse)) {

      var resources = resourcesPath.<JSONArray>eval(jsonResponse, JSONArray.class);

      var allWebResources = Stream.concat(
          datasource.getResources().stream(),
          enrichedWebResourcesStream(datasource, resources)
      ).collect(toList());

      return DatasourceEnrichment.<WebServerDatasourceDTO>builder()
          .metadata(buildMetadata(resources))
          .datasource(datasource.toBuilder().resources(allWebResources).build())
          .build();
    } else {
      return buildIdentity(datasource);
    }

  }

  private DatasourceEnrichment<WebServerDatasourceDTO> buildIdentity(
      WebServerDatasourceDTO datasource) {
    return DatasourceEnrichment.<WebServerDatasourceDTO>builder()
        .metadata(emptySet())
        .datasource(datasource)
        .build();
  }


  private Set<Metadata> buildMetadata(JSONArray resources) {
    var titlePath = JSONPath.compile("$.title");
    var keyPath = JSONPath.compile(KEY_PATH);

    return resources.stream()
        .map(r -> buildResourceForKey(r, keyPath))
        .map(r -> Set.of(
                Metadata.builder()
                    .name(r.getKey() + ":title")
                    .value(titlePath.eval(r.getValue(), String.class))
                    .type("string")
                    .build()
            )
        ).flatMap(Set::stream)
        .collect(toSet());
  }

  private <T> SimpleEntry<String, T> buildResourceForKey(T resource, JSONPath keyPath) {
    return new SimpleEntry<>(
        "document:" + keyPath.eval(resource, String.class),
        resource
    );
  }

  private Stream<WebServerResource> enrichedWebResourcesStream(WebServerDatasourceDTO datasource,
      JSONArray resources) {
    var linkPath = JSONPath.compile("$._links.self.href");
    var baseUrl = datasource.getBaseUrl();

    return resources.stream()
        .map(r -> buildResourceForKey(r, JSONPath.compile(KEY_PATH)))
        .map(r -> WebServerResource.builder()
            .url(
                StringUtils.removeStart(
                    linkPath.eval(r.getValue(), String.class),
                    baseUrl
                )
            ).keywords(Set.of(r.getKey()))
            .build()
        );
  }

}
