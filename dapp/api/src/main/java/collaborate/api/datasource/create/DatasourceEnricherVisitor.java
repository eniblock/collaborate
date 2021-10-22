package collaborate.api.datasource.create;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceEnrichment;
import collaborate.api.datasource.model.dto.DatasourcePurpose;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

// Does JSONPath are thread safe and can be put at class level instead of method level ?
@Component
@RequiredArgsConstructor
@Slf4j
public class DatasourceEnricherVisitor implements
    DatasourceDTOVisitor<DatasourceEnrichment<? extends DatasourceDTO>> {

  public static final String RESOURCE_ID_PATH = "$.id";
  private final HttpURLConnectionFactory httpUrlConnectionFactory;

  @Override
  public DatasourceEnrichment<? extends DatasourceDTO> visitWebServerDatasource(
      WebServerDatasourceDTO webServerDatasourceDTO)
      throws DatasourceVisitorException {

    if (DatasourcePurpose.BUSINESS_DATA.match(webServerDatasourceDTO)) {
      return enrichBusinessData(webServerDatasourceDTO);
    }
    return buildIdentity(webServerDatasourceDTO);
  }

  private DatasourceEnrichment<WebServerDatasourceDTO> enrichBusinessData(
      WebServerDatasourceDTO webServerDatasourceDTO)
      throws DatasourceVisitorException {

    var response = getAssetListResponse(webServerDatasourceDTO);
    return build(webServerDatasourceDTO, response);
  }

  private String getAssetListResponse(WebServerDatasourceDTO webServerDatasourceDTO)
      throws DatasourceVisitorException {
    var httpURLConnection = httpUrlConnectionFactory.create(
        webServerDatasourceDTO,
        SCOPE_ASSET_LIST
    );

    try {
      httpURLConnection.connect();

      log.debug("fetching url={}, responseCode={}",
          httpURLConnection.getURL(),
          httpURLConnection.getResponseCode()
      );

      if (httpURLConnection.getResponseCode() != OK.value()) {
        throw new ResponseStatusException(BAD_REQUEST,
            "Getting business data documents failed with responseCode="
                + httpURLConnection.getResponseCode());
      }
      return httpURLConnection.getResponseMessage();
    } catch (IOException e) {
      httpURLConnection.disconnect();
      log.error("While testing connection to URL=" + httpURLConnection.getURL(), e);
      throw new DatasourceVisitorException(e);
    } finally {
      httpURLConnection.disconnect();
    }
  }

  DatasourceEnrichment<WebServerDatasourceDTO> build(WebServerDatasourceDTO datasource,
      String jsonResponse) {
    var resourcesPath = JSONPath.compile("$._embedded.resources");
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
    var scopePath = JSONPath.compile("$.scope");

    return resources.stream()
        .map(r -> buildResourceForKey(r, JSONPath.compile(RESOURCE_ID_PATH)))
        .map(r -> Set.of(
            Metadata.builder()
                .name(r.getKey() + ":title")
                .value(titlePath.eval(r.getValue(), String.class))
                .build(),
            Metadata.builder()
                .name(r.getKey() + ":scope")
                .value(scopePath.eval(r.getValue(), String.class))
                .build())
        ).flatMap(Set::stream)
        .collect(toSet());
  }

  private <T> SimpleEntry<String, T> buildResourceForKey(T resource, JSONPath idPath) {
    return new SimpleEntry<>(
        "document:" + idPath.eval(resource, String.class),
        resource
    );
  }

  private Stream<WebServerResource> enrichedWebResourcesStream(WebServerDatasourceDTO datasource,
      JSONArray resources) {
    var linkPath = JSONPath.compile("$._links.self.href");
    var baseUrl = datasource.getBaseUrl();

    return resources.stream()
        .map(r -> buildResourceForKey(r, JSONPath.compile(RESOURCE_ID_PATH)))
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
