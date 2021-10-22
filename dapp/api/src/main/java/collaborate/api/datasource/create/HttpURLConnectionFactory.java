package collaborate.api.datasource.create;

import collaborate.api.datasource.URIFactory;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.http.HttpURLConnectionVisitorFactory;
import java.net.HttpURLConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HttpURLConnectionFactory {

  private final HttpURLConnectionVisitorFactory httpURLConnectionVisitorFactory;
  private final URIFactory uriFactory;

  public HttpURLConnection create(WebServerDatasourceDTO serverDatasourceDTO,
      String resourceKeyword) {
    var resource = serverDatasourceDTO.getResourceByKeywordOrThrow(resourceKeyword);
    var uri = uriFactory.create(serverDatasourceDTO, resource);

    var httpURLConnectionVisitor = httpURLConnectionVisitorFactory.create(uri);
    return serverDatasourceDTO.getAuthMethod().accept(httpURLConnectionVisitor);
  }
}
