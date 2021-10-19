package collaborate.api.gateway;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.http.HttpClientFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Repository
public class GatewayUrlDAO {

  private final TraefikProperties traefikProperties;
  private final HttpClientFactory httpClientFactory;
  private final ObjectMapper objectMapper;

  public JsonNode fetch(String url) throws IOException {
    var httpClient = httpClientFactory.createTrustAllAndNoHostnameVerifier();
    var response = httpClient.execute(new HttpGet(url));
    if (HttpStatus.OK.value() != response.getStatusLine().getStatusCode()) {
      throw new ResponseStatusException(BAD_GATEWAY, "While fetching url=" + url);
    }
    return objectMapper.readTree(EntityUtils.toString(response.getEntity()));
  }
}
