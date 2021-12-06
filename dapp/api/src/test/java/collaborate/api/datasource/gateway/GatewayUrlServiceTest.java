package collaborate.api.datasource.gateway;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GatewayUrlServiceTest {

  @Mock
  GatewayUrlDAO gatewayUrlDAO;
  @Mock
  TraefikProperties traefikProperties;
  @Mock
  UserMetadataService userMetadataService;

  @InjectMocks
  GatewayUrlService gatewayService;

  @Test
  void fetch_shouldCallGatewayUrlDAOFetchWithExpectedURL() {
    // GIVEN
    String assetId = "Emoo8Bae";
    String datasourceUUID = "e0cbb503-7173-4330-898d-1fa9c525b33b";
    when(traefikProperties.getUrl())
        .thenReturn("https://localhost:8443");
    when(userMetadataService.find(datasourceUUID, VaultMetadata.class))
        .thenReturn(Optional.empty());
    // WHEN
    var gatewayResource = GatewayResourceDTO.builder()
        .assetIdForDatasource(assetId)
        .scope("kilometer")
        .datasourceId(datasourceUUID)
        .build();
    gatewayService.fetch(gatewayResource);

    // THEN
    verify(gatewayUrlDAO, times(1)).fetch(
        "https://localhost:8443/datasource/" + datasourceUUID + "/kilometer/" + assetId,
        Optional.empty());

  }

}
