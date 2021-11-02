package collaborate.api.http;

import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HttpClientFactory {

  public CloseableHttpClient createTrustAllAndNoHostnameVerifier() {
    try {
      return HttpClients
          .custom()
          .setSSLContext(
              new SSLContextBuilder()
                  .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                  .build()
          ).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          .setRedirectStrategy(new LaxRedirectStrategy())
          .build();
    } catch (Exception e) {
      log.error("createTrustAllAndNoHostnameVerifier", e);
      throw new IllegalStateException(e);
    }
  }

  public CloseableHttpClient createNoHostnameVerifier(SSLContext sslContext) {
    try {
      return HttpClients
          .custom()
          .setSSLContext(sslContext)
          .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
          .setRedirectStrategy(new LaxRedirectStrategy())
          .build();
    } catch (Exception e) {
      log.error("createTrustAllAndNoHostnameVerifier", e);
      throw new IllegalStateException(e);
    }
  }
}
