package collaborate.api.datasource.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.nft.AssetScopeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GatewayUrlServiceTest {

  @Mock
  AssetScopeRepository assetScopeRepository;
  @Mock
  AuthenticationService authenticationService;
  @Mock
  GatewayUrlDAO gatewayUrlDAO;
  @Mock
  TraefikProperties traefikProperties;

  @InjectMocks
  GatewayUrlService gatewayService;

  @Test
  void buildURL_returnsExpected() {
    // GIVEN
    String assetId = "Emoo8Bae";
    String datasourceUUID = "e0cbb503-7173-4330-898d-1fa9c525b33b";
    when(traefikProperties.getUrl())
        .thenReturn("https://localhost:8443");
    // WHEN
    var gatewayResource = GatewayResourceDTO.builder()
        .assetIdForDatasource(assetId)
        .alias("kilometer")
        .datasourceId(datasourceUUID)
        .build();
    String urlResult = gatewayService.buildURL(gatewayResource);

    // THEN
    assertThat(urlResult).isEqualTo(
        "https://localhost:8443/datasource/" + datasourceUUID + "/kilometer/" + assetId);
  }

}
