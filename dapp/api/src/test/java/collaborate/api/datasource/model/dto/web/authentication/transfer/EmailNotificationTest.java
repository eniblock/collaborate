package collaborate.api.datasource.model.dto.web.authentication.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class EmailNotificationTest {

  @Test
  void deserializeAsInterface_shouldReturnExpectedImplementation() {
    // WHEN
    var transferMethod = TestResources.readContent(
        "/datasource/model/web/authentication/transfer/certificateBasedAuthorityEmail.json",
        PartnerTransferMethod.class);

    // THEN
    assertThat(transferMethod).isEqualTo(new EmailNotification("mail@test.com"));
  }
}
