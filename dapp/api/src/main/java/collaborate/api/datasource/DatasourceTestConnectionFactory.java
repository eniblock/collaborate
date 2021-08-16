package collaborate.api.datasource;

import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.http.HttpURLConnectionFactory;
import collaborate.api.http.HttpURLConnectionPredicate;
import collaborate.api.http.security.SSLContextException;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DatasourceTestConnectionFactory {

  private final HttpURLConnectionFactory httpURLConnectionFactory;

  public BooleanSupplier create(DataSource datasource)
      throws SSLContextException, IOException, UnrecoverableKeyException {
    if (datasource instanceof WebServerDatasource) {
      var webServerDatasource = (WebServerDatasource) datasource;
      var isDigitalPassword = webServerDatasource.getKeywords().stream()
          .anyMatch(s -> s.equals("digital-passport"));

      if (isDigitalPassword) {
        return testDigitalPassportConnection(datasource, webServerDatasource);
      }
    }
    throw new NotYetImplementedException();
  }

  private BooleanSupplier testDigitalPassportConnection(DataSource datasource, WebServerDatasource webServerDatasource)
      throws IOException, SSLContextException, UnrecoverableKeyException {
    var assetResource = webServerDatasource.getResources().stream()
        .filter(r -> r.getKeywords().contains("assets"))
        .findFirst();

    if (assetResource.isPresent()) {
      var resource = assetResource.get();
      var httpURLConnection = httpURLConnectionFactory
          .create(resource.getPath(), datasource.getAuthMethod());
      return () -> new HttpURLConnectionPredicate().test(httpURLConnection);
    } else {
      throw new IllegalStateException(
          "A digital-passport datasource should define an assets resource");
    }
  }

}
