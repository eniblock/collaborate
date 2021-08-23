package collaborate.api.datasource;

import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.WebServerResource;
import collaborate.api.datasource.domain.web.authentication.BasicAuth;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class URIFactory {

  public URI create(WebServerDatasource webServerDatasource,
      WebServerResource resource) {
    var uriBuilder = UriComponentsBuilder
        .fromUriString(webServerDatasource.getBaseUrl() + resource.getUrl());

    if (resource.getQueryParams() != null) {
      resource.getQueryParams().forEach(q -> uriBuilder.queryParam(q.getKey(), q.getValue()));
    }

    if (webServerDatasource.getAuthMethod() instanceof BasicAuth) {
      var basicAuthQueryParams = ((BasicAuth) webServerDatasource.getAuthMethod())
          .getQueryParams();
      if (basicAuthQueryParams != null) {
        basicAuthQueryParams.forEach(q -> uriBuilder.queryParam(q.getKey(), q.getValue()));
      }
    }

    return uriBuilder.build().toUri();
  }
}
