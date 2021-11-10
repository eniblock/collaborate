package collaborate.api.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
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
  HttpServletRequest httpServletRequest;
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
    String traefikURL =
        "/api/v1/gateway/datasource/e0cbb503-7173-4330-898d-1fa9c525b33b/kilometer/" + assetId;
    when(httpServletRequest.getRequestURI())
        .thenReturn(traefikURL);
    when(traefikProperties.getUrl())
        .thenReturn("https://localhost:8443");
    when(userMetadataService.findMetadata(datasourceUUID, VaultMetadata.class))
        .thenReturn(Optional.empty());
    // WHEN
    gatewayService.fetch(datasourceUUID, httpServletRequest);

    // THEN
    verify(gatewayUrlDAO, times(1)).fetch(
        "https://localhost:8443/datasource/" + datasourceUUID + "/kilometer/" + assetId,
        Optional.empty());

  }

  @Test
  void replaceBaseUrl_shouldReturnExpectedUri_withTraefikUrlEndingByASlashe() {
    // GIVEN
    when(httpServletRequest.getRequestURI()).thenReturn("/api/v1/gateway/datasource/");
    when(traefikProperties.getUrl()).thenReturn("http://localhost:3000/");
    // WHEN
    String replacementResult = gatewayService.replaceBaseUrl(httpServletRequest);
    // THEN
    assertThat(replacementResult).isEqualTo("http://localhost:3000/datasource/");
  }

}
