package collaborate.api.config.security;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Order(0)
class EnableSelfSignedCertificates implements ApplicationListener<ApplicationReadyEvent> {

  @SneakyThrows
  @Override
  public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
    SSLContext sslContext = SSLContexts
        .custom()
        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
        .build();
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
  }

}