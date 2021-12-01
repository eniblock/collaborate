package collaborate.api.datasource.model.dto.web.authentication.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class OAuth2RefreshTokenTest {

  @Test
  void deserializeAsInterface_shouldReturnExpectedImplementation() {
    // WHEN
    var transferMethod = TestResources.readContent(
        "/datasource/model/web/authentication/transfer/oAuth2RefreshToken.json",
        PartnerTransferMethod.class);

    // THEN
    assertThat(transferMethod).isInstanceOf(OAuth2RefreshToken.class);
  }
}
