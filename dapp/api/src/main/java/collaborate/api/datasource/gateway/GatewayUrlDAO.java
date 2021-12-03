package collaborate.api.datasource.gateway;

import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.http.RequestEntityBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
@Repository
class GatewayUrlDAO {

  private final HttpClientFactory httpClientFactory;

  public ResponseEntity<JsonNode> fetch(String url, Optional<AccessTokenResponse> oAuth2Jwt) {
    RestTemplate restTemplate = buildRestTemplate();
    var requestEntityBuilder = new RequestEntityBuilder<>(url);
    oAuth2Jwt.ifPresent(requestEntityBuilder::jwt);
    return restTemplate.exchange(requestEntityBuilder.build(), JsonNode.class);
  }

  private RestTemplate buildRestTemplate() {
    var restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(
        new HttpComponentsClientHttpRequestFactory(
            httpClientFactory.createTrustAllAndNoHostnameVerifier()
        )
    );
    return restTemplate;
  }
}