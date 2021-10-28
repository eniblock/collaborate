package collaborate.api.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

@Slf4j
@NoArgsConstructor
public class HttpUrlConnectionBuilder {

  private String url;
  private String requestMethod = "GET";
  private SSLContext sslContext;
  private final HttpHeaders headers = new HttpHeaders();
  private BasicAuthHeader basicAuthHeader;
  private String postData;

  public HttpUrlConnectionBuilder(String url) {
    this.url = url;
  }

  public HttpUrlConnectionBuilder sslContext(SSLContext sslContext) {
    this.sslContext = sslContext;
    return this;
  }

  public HttpUrlConnectionBuilder header(String key, String... values) {
    this.headers.put(key, Arrays.asList(values));
    return this;
  }

  public HttpUrlConnectionBuilder authorizationBasic(String user, String password) {
    basicAuthHeader = new BasicAuthHeader(user, password);
    return header("Authorization", basicAuthHeader.getValue());
  }

  public HttpUrlConnectionBuilder requestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  public HttpUrlConnectionBuilder body(Map<String, String> body) {
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : body.entrySet()) {
      if (first) {
        first = false;
      } else {
        result.append("&");
      }
      result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
      result.append("=");
      result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
    }
    this.postData = result.toString();
    return this;
  }

  public HttpURLConnection build() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod(requestMethod);

    if (connection instanceof HttpsURLConnection) {
      if (sslContext == null) {
        log.warn(
            "sslContext should be provided when working with https protocol, url={" + url + "}");
      } else {
        ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
      }
    }

    headers.forEach((key, value) ->
        connection.setRequestProperty(key, String.join(", ", value))
    );
    if (StringUtils.equalsIgnoreCase(this.requestMethod, "POST")) {
      var encodedPostData = this.postData.getBytes(StandardCharsets.UTF_8);
      connection.setRequestProperty("Content-Length", Integer.toString(encodedPostData.length));
      connection.setDoOutput(true);
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.write(encodedPostData);
      }
    }
    if (basicAuthHeader != null) {
      connection.setRequestProperty("Authorization", basicAuthHeader.getValue());
    }
    return connection;
  }
}
