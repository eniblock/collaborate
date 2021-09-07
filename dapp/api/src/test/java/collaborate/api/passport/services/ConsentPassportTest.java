package collaborate.api.passport.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import collaborate.api.passport.PassportService;
import collaborate.api.passport.consent.ConsentPassportDAO;
import collaborate.api.passport.consent.ConsentPassportDTO;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Job.Status;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.security.ConnectedUserDAO;
import collaborate.api.user.tag.TagUserDAO;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConsentPassportTest {

  @Mock
  private ConsentPassportDAO consentPassportDAO;
  @Mock
  private ConnectedUserDAO connectedUserDAO;
  @Mock
  private TagUserDAO tagUserDAO;
  @InjectMocks
  private PassportService passportService;

  @Test
  void consent_Ok() {
    // GIVEN
    Job jobResult = Job.builder()
        .id(2)
        .status(Status.CREATED)
        .build();

    Integer contractId = 25;
    String userEmail = "pcc-development@theblockchainxdev.com";
    String userTagId = "pcc-development_._xdev-at_._theblockchainxdev.com";
    ConsentPassportDTO consentPassportDTO = ConsentPassportDTO.builder()
        .vehicleOwnerUserWallet(
            UserWalletDTO.builder()
                .address("tzVehicleOwner")
                .userId(userTagId)
                .email(userEmail)
                .build()
        ).contractId(contractId)
        .build();
    when(connectedUserDAO.getEmailOrThrow()).thenReturn(userEmail);
    when(tagUserDAO.findOneByUserEmail(userEmail))
        .thenReturn(Optional.of(consentPassportDTO.getVehicleOwnerUserWallet()));
    when(consentPassportDAO.consent(eq(consentPassportDTO))).thenReturn(jobResult);
    // WHEN
    Job actual = passportService.consent(contractId);
    // THEN
    assertThat(actual.getId()).isEqualTo(jobResult.getId());
    assertThat(actual.getStatus()).isEqualTo(jobResult.getStatus());
  }


}
