package collaborate.api.passport.consent;

import collaborate.api.tag.model.job.Job;
import collaborate.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsentService {

  private final ConsentPassportDAO consentPassportDAO;
  private final UserService userService;

  public Job consent(Integer contractId) {
    return consentPassportDAO.consent(ConsentPassportDTO.builder()
        .contractId(contractId)
        .vehicleOwnerUserWallet(
            userService.getConnectedUserWallet()
        ).build()
    );
  }
}
