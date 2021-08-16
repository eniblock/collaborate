package collaborate.api.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@NoArgsConstructor
public class HttpURLConnectionBuilder {

  private String url;
  private String requestMethod = "GET";
  private SSLContext sslContext;
  private final HttpHeaders headers = new HttpHeaders();

  public HttpURLConnectionBuilder(String url) {
    this.url = url;
  }

  public HttpURLConnectionBuilder sslContext(SSLContext sslContext) {
    this.sslContext = sslContext;
    return this;
  }

  public HttpURLConnectionBuilder header(String key, String... values) {
    this.headers.put(key, Arrays.asList(values));
    return this;
  }

  public HttpURLConnectionBuilder authorizationBasic(String user, String password) {
    var basicToken = user + ":" + password;
    var encodedBasicToken = Base64.getEncoder().encodeToString(basicToken.getBytes());
    return header("Authorization", "Basic " + encodedBasicToken);
  }

  public HttpURLConnectionBuilder header(String requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  public HttpURLConnection build() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod(requestMethod);

    if (connection instanceof HttpsURLConnection) {
      if (sslContext == null) {
        throw new IllegalArgumentException(
            "sslContext should be provided when working with https protocol, url={" + url + "}");
      }
      ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
    }

    headers.forEach((key, value) ->
        connection.setRequestProperty(key, String.join(", ", value))
    );

    return connection;

  }
}
