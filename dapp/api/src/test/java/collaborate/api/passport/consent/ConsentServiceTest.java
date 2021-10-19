package collaborate.api.passport.consent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Job.Status;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConsentServiceTest {

  @Mock
  private ConsentPassportDAO consentPassportDAO;
  @Mock
  private UserService userService;
  @InjectMocks
  private ConsentService consentService;

  // FIXME rename this test
  @Test
  void consent_Ok() {
    // GIVEN
    Job jobResult = Job.builder()
        .id(2)
        .status(Status.CREATED)
        .build();

    Integer  contractId = 25;
    String userEmail = "development@theblockchainxdev.com";
    String userTagId = "development_._xdev-at_._theblockchainxdev.com";
    UserWalletDTO userWalletDTO = UserWalletDTO.builder()
        .address("tzVehicleOwner")
        .userId(userTagId)
        .email(userEmail)
        .build();
    ConsentPassportDTO consentPassportDTO = new ConsentPassportDTO(contractId, userWalletDTO);
    when(userService.getConnectedUserWallet()).thenReturn(userWalletDTO);
    when(consentPassportDAO.consent(eq(consentPassportDTO))).thenReturn(jobResult);
    // WHEN
    Job actual = consentService.consent(contractId);
    // THEN
    assertThat(actual.getId()).isEqualTo(jobResult.getId());
    assertThat(actual.getStatus()).isEqualTo(jobResult.getStatus());
  }


}
