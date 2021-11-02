package collaborate.api.http;

import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import java.net.URI;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;

public class RequestEntityBuilder<T> {

  public static final String AUTHORIZATION = "Authorization";
  private String url;
  private HttpMethod requestMethod = HttpMethod.GET;
  private final HttpHeaders headers = new HttpHeaders();
  private T postData;

  public RequestEntityBuilder(String url) {
    this.url = url;
  }

  public RequestEntityBuilder<T> header(String key, String... values) {
    this.headers.put(key, Arrays.asList(values));
    return this;
  }

  public RequestEntityBuilder<T> authorizationBasic(String user, String password) {
    return header(AUTHORIZATION, new BasicAuthHeader(user, password).getValue());
  }

  public RequestEntityBuilder<T> jwt(AccessTokenResponse accessTokenResponse) {
    return header(AUTHORIZATION, accessTokenResponse.getBearerHeaderValue());
  }

  public RequestEntityBuilder<T> requestMethod(String requestMethod) {
    this.requestMethod = HttpMethod.resolve(requestMethod);
    return this;
  }

  public RequestEntity<?> build() {
    BodyBuilder requestBuilder = RequestEntity
        .method(requestMethod, URI.create(url))
        .accept(MediaType.ALL)
        .headers(headers);

    RequestEntity<?> requestEntity = requestBuilder.build();
    if (this.requestMethod == HttpMethod.POST) {
      requestEntity = requestBuilder.body(postData);
    }

    return requestEntity;
  }


}
