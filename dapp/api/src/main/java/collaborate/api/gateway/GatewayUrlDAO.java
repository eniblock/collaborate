package collaborate.api.gateway;

import static collaborate.api.cache.CacheConfig.CacheNames.VEHICLE_ID;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.http.HttpClientFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Slf4j
@Repository
public class GatewayUrlDAO {

  public static final String VIN_MAPPING_ROUTING_KEY = "vin-mapping";
  private final ApiProperties apiProperties;
  private final HttpClientFactory httpClientFactory;
  private final ObjectMapper objectMapper;

  @Cacheable(value = VEHICLE_ID)
  public String getVehicleId(String datasourceUUID, String vin, String vehicleIdJsonResponsePath,
      Optional<String> queryParam)
      throws IOException {

    var httpClient = httpClientFactory.createTrustAllAndNoHostnameVerifier();
    String vehiclesURI = buildVehiclesIdByVinUrl(datasourceUUID, vin, queryParam);
    var response = httpClient.execute(new HttpGet(vehiclesURI));
    var responseBody = EntityUtils.toString(response.getEntity());
    int statusCode = response.getStatusLine().getStatusCode();
    if (HttpStatus.OK.value() == statusCode) {
      return JsonPath.read(responseBody, vehicleIdJsonResponsePath);
    } else {
      log.error("Unexpected httpResponse status={}, body={}", statusCode, responseBody);
      throw new ResponseStatusException(
          BAD_GATEWAY,
          format("While getting vehicleId in datasource=%s for VIN=%s", datasourceUUID, vin)
      );
    }
  }

  String buildVehiclesIdByVinUrl(String datasourceUUID, String vin,
      Optional<String> queryParam) {
    var uriBuilder = UriComponentsBuilder.fromUriString(
        apiProperties.getTraefik().getUrl()
            + "/datasource/" + datasourceUUID
            + "/" + VIN_MAPPING_ROUTING_KEY
    );

    if (queryParam.isPresent()) {
      uriBuilder = uriBuilder.queryParam(queryParam.get(), vin);
    }

    return uriBuilder.build().toUriString();
  }

  public JsonNode fetch(String url) throws IOException {
    var httpClient = httpClientFactory.createTrustAllAndNoHostnameVerifier();
    var response = httpClient.execute(new HttpGet(url));
    if (HttpStatus.OK.value() != response.getStatusLine().getStatusCode()) {
      throw new ResponseStatusException(BAD_GATEWAY, "While fetching url=" + url);
    }
    return objectMapper.readTree(EntityUtils.toString(response.getEntity()));
  }
}
