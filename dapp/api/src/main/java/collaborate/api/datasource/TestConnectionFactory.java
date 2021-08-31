package collaborate.api.datasource;

import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.domain.web.WebServerDatasource;
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

  public BooleanSupplier create(Datasource datasource)
      throws SSLContextException, IOException, UnrecoverableKeyException {

    if (datasource instanceof WebServerDatasource) {
      var webServerDatasource = (WebServerDatasource) datasource;
      var isDigitalPassword = webServerDatasource.anyKeywordsContains("digital-passport");
      if (isDigitalPassword) {
        return testDigitalPassportConnection(webServerDatasource);
      }
    }
    throw new NotYetImplementedException();
  }

  private BooleanSupplier testDigitalPassportConnection(WebServerDatasource webServerDatasource)
      throws IOException, SSLContextException, UnrecoverableKeyException {
    var resource = webServerDatasource.findResourceByKeywordOrThrow("assets");
    var uri = uriFactory.create(webServerDatasource, resource);

    var httpURLConnection = httpURLConnectionFactory
        .create(uri, webServerDatasource.getAuthMethod());
    return () -> responseCodeOkPredicate.test(httpURLConnection);
  }

}
