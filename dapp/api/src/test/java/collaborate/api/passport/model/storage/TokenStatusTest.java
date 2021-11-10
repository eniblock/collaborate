package collaborate.api.passport.model.storage;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.passport.model.TokenStatus;
import org.junit.jupiter.api.Test;

class TokenStatusTest {

  @Test
  void fromMultisig_shoudReturnPendingCreation_withMultisigOkNull() {
    // GIVEN
    var multisig = Multisig.builder()
        .ok(null)
        .build();
    // WHEN
    var currentTokenStatus = TokenStatus.from(multisig);
    // THEN
    assertThat(currentTokenStatus).isEqualTo(TokenStatus.PENDING_CREATION);
  }

  @Test
  void fromMultisig_shoudReturnPendingCreation_withMultisigOkFalse() {
    // GIVEN
    var multisig = Multisig.builder()
        .ok(false)
        .build();
    // WHEN
    var currentTokenStatus = TokenStatus.from(multisig);
    // THEN
    assertThat(currentTokenStatus).isEqualTo(TokenStatus.PENDING_CREATION);
  }

  @Test
  void fromMultisig_shoudReturnPendingCreation_withMultisigOkTrue() {
    // GIVEN
    var multisig = Multisig.builder()
        .ok(true)
        .build();
    // WHEN
    var currentTokenStatus = TokenStatus.from(multisig);
    // THEN
    assertThat(currentTokenStatus).isEqualTo(TokenStatus.CREATED);
  }
}
