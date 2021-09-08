package collaborate.api.gateway;

import static collaborate.api.gateway.GatewayUrlDAO.VIN_MAPPING_ROUTING_KEY;
import static collaborate.api.test.TestResources.objectMapper;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.config.api.TraefikProperties;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.test.TestResources;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class GatewayUrlDAOTest {

  @Mock
  ApiProperties apiProperties;
  @Mock
  HttpClientFactory httpClientFactory;
  @Mock
  CloseableHttpClient httpClient;
  @Mock
  CloseableHttpResponse httpResponse;
  @Mock
  HttpEntity httpEntity;

  GatewayUrlDAO gatewayURLDAO;

  final String vehicleIdJsonPath = "$._embedded.vehicles[0].id";
  final String datasourceUUID = "e0cbb503-7173-4330-898d-1fa9c525b33b";
  final String vin = "VR1ATTENTKW033329";
  final String vehicleId = "124091f9115613c465747647191cfa4d0d97bc3e1e4ae2f1cb3d192c3c36e183e6620c36bd6bb6d0115a436ce28529b79";
  final TraefikProperties traefikProperties = TraefikProperties.builder()
      .url("https://localhost:8443")
      .build();

  @BeforeEach
  void setUp() {
    gatewayURLDAO = new GatewayUrlDAO(apiProperties, httpClientFactory, objectMapper);
  }

  @Test
  void getVehicleId_shouldReturnExpectedVehicleId() throws IOException {
    // GIVEN
    when(apiProperties.getTraefik()).thenReturn(traefikProperties);
    when(httpClientFactory.createTrustAllAndNoHostnameVerifier()).thenReturn(httpClient);
    when(httpClient.execute(any())).thenReturn(httpResponse);
    when(httpResponse.getStatusLine()).thenReturn(
        new BasicStatusLine(
            new ProtocolVersion("https", 1, 1),
            HttpStatus.OK.value(),
            HttpStatus.OK.getReasonPhrase()
        )
    );
    when(httpResponse.getEntity()).thenReturn(httpEntity);
    when(httpEntity.getContent())
        .thenReturn(
            IOUtils.toInputStream(
                TestResources.read("/gateway/psa.vehiclesByVinPrefixResponse.json"),
                StandardCharsets.UTF_8)
        );
    when(httpEntity.getContentType())
        .thenReturn(new BasicHeader("content-type", APPLICATION_JSON.toString()));

    // WHEN
    String vehicleIdResult = gatewayURLDAO
        .getVehicleId(datasourceUUID, vin, vehicleIdJsonPath, Optional.of("queryParam"));
    // THEN

    assertThat(vehicleIdResult).isEqualTo(vehicleId);
  }

  @Test
  void buildVehiclesIdByVinUrl_shouldReturnExpectedVehiclesIdByVinUrl() {
    // GIVEN
    String datasourceUUID = "ds";
    String vin = "tested-vin";
    Optional<String> queryParam = Optional.of("vinPrefix");
    when(apiProperties.getTraefik()).thenReturn(traefikProperties);
    // WHEN
    var vehiclesIdByVinUrlResult = gatewayURLDAO
        .buildVehiclesIdByVinUrl(datasourceUUID, vin, queryParam);
    // THEN
    assertThat(vehiclesIdByVinUrlResult)
        .isEqualTo(
            "https://localhost:8443/datasource/ds/"
                + VIN_MAPPING_ROUTING_KEY
                + "?vinPrefix=tested-vin"
        );
  }

  @Test
  void jsonPath_shouldGetTheFirstVehicleId() {
    String vid = JsonPath.read(
        TestResources.read("/gateway/psa.vehiclesByVinPrefixResponse.json"),
        vehicleIdJsonPath);
    assertThat(vid).isEqualTo(vid);
  }
}
