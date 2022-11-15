package collaborate.api.datasource.gateway;

import collaborate.api.http.HttpClientFactory;
import collaborate.api.http.RequestEntityBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
@Repository
class GatewayUrlDAO {

  private final HttpClientFactory httpClientFactory;

  public ResponseEntity<String> fetch(String url, Optional<String> authorizationHeader) {
    RestTemplate restTemplate = buildRestTemplate();
    var requestEntityBuilder = new RequestEntityBuilder<>(url);
    authorizationHeader.ifPresent(header ->
        requestEntityBuilder.header(RequestEntityBuilder.AUTHORIZATION, header)
    );
    var res = requestEntityBuilder.build();
    log.info(res.toString());
    return restTemplate.exchange(res, String.class);
  }

  private RestTemplate buildRestTemplate() {
    var restTemplate = new RestTemplate();
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
    messageConverters.add(converter);
    //restTemplate.setMessageConverters(messageConverters);
    restTemplate.setRequestFactory(
        new HttpComponentsClientHttpRequestFactory(
            httpClientFactory.createTrustAllAndNoHostnameVerifier()
        )
    );
    return restTemplate;
  }
}
