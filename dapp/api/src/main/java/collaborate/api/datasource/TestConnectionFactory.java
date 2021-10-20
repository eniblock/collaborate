package collaborate.api.datasource;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.PURPOSE_TEST_CONNECTION;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.http.HttpURLConnectionFactory;
import collaborate.api.http.ResponseCodeOkPredicate;
import collaborate.api.http.security.SSLContextException;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TestConnectionFactory {

  private final HttpURLConnectionFactory httpURLConnectionFactory;
  private final ResponseCodeOkPredicate responseCodeOkPredicate;
  private final URIFactory uriFactory;

  public BooleanSupplier create(DatasourceDTO datasource)
      throws SSLContextException, IOException, UnrecoverableKeyException {
    if (datasource instanceof WebServerDatasourceDTO) {
      return testWebServerConnectionSupplier((WebServerDatasourceDTO) datasource);
    }
    throw new NotYetImplementedException();
  }

  private BooleanSupplier testWebServerConnectionSupplier(
      WebServerDatasourceDTO webServerDatasource)
      throws IOException, SSLContextException, UnrecoverableKeyException {
    var resource = webServerDatasource.getResourceByKeywordOrThrow(PURPOSE_TEST_CONNECTION);
    var uri = uriFactory.create(webServerDatasource, resource);

    var httpURLConnection = httpURLConnectionFactory
        .create(uri, webServerDatasource.getAuthMethod());
    return () -> responseCodeOkPredicate.test(httpURLConnection);
  }

}
