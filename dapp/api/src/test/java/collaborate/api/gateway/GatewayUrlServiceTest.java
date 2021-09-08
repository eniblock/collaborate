package collaborate.api.gateway;

import static collaborate.api.gateway.GatewayUrlService.BASE_URL_GROUP_INDEX;
import static collaborate.api.gateway.GatewayUrlService.DATASOURCE_UUID_GROUP_INDEX;
import static collaborate.api.gateway.GatewayUrlService.VIN_GROUP_INDEX;
import static collaborate.api.gateway.GatewayUrlService.VIN_REGEXP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.WebServerResource;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GatewayUrlServiceTest {

  @Mock
  ApiProperties apiProperties;
  @Mock
  DatasourceService datasourceService;
  @Mock
  GatewayUrlDAO gatewayUrlDAO;

  @Mock
  HttpServletRequest httpServletRequest;
  @Mock
  WebServerDatasource webServerDatasource;
  @Mock
  WebServerResource webServerResource;

  @InjectMocks
  GatewayUrlService gatewayVINService;

  @Test
  void fetch_shouldCallGatewayUrlDAOFetchWithExpectedURL() throws IOException {
    // GIVEN
    String vin = "VR1ATTENTKW033329";
    String datasourceUUID = "e0cbb503-7173-4330-898d-1fa9c525b33b";
    String vehicleId = "124091f9115613c465747647191cfa4d0d97bc3e1e4ae2f1cb3d192c3c36e183e6620c36bd6bb6d0115a436ce28529b79";
    String traefikURL =
        "/api/v1/gateway/datasource/e0cbb503-7173-4330-898d-1fa9c525b33b/kilometer/" + vin;
    when(httpServletRequest.getRequestURI()).thenReturn(traefikURL);
    when(apiProperties.getTraefik())
        .thenReturn(new TraefikProperties(null, null, "https://localhost:8443"));

    when(datasourceService.getWebServerDatasourceByUUID(datasourceUUID)).thenReturn(webServerDatasource);
    when(webServerDatasource.findVinMappingResource()).thenReturn(Optional.of(webServerResource));
    when(webServerResource.findVinMappingQueryParamKey()).thenReturn(Optional.of("vinPrefix"));

    when(gatewayUrlDAO.getVehicleId(eq(datasourceUUID), eq(vin), any(), any()))
        .thenReturn(vehicleId);
    // WHEN
    gatewayVINService.fetch(httpServletRequest);

    // THEN
    verify(gatewayUrlDAO, times(1)).getVehicleId(eq(datasourceUUID), eq(vin), any(), any());
    verify(gatewayUrlDAO, times(1)).fetch(
        eq("https://localhost:8443/datasource/" + datasourceUUID + "/kilometer/" + vehicleId)
    );

  }

  @Test
  void VIN_REGEXP_shouldMatch2groups() {
    // GIVEN
    Matcher matcher = VIN_REGEXP.matcher(
        "https://localhost:8443/datasource/e0cbb503-7173-4330-898d-1fa9c525b33b/kilometer/VR1ATTENTKW033329?toto");
    matcher.find();
    // WHEN
    assertThat(matcher.groupCount()).isEqualTo(3);
    assertThat(matcher.group(BASE_URL_GROUP_INDEX))
        .isEqualTo("https://localhost:8443/datasource/e0cbb503-7173-4330-898d-1fa9c525b33b/");
    assertThat(matcher.group(DATASOURCE_UUID_GROUP_INDEX))
        .isEqualTo("e0cbb503-7173-4330-898d-1fa9c525b33b");
    assertThat(matcher.group(VIN_GROUP_INDEX)).isEqualTo("VR1ATTENTKW033329");
  }

}
