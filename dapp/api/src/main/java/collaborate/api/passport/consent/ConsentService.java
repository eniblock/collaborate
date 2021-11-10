package collaborate.api.passport.consent;

import collaborate.api.tag.model.job.Job;
import collaborate.api.user.connected.ConnectedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsentService {

  private final ConnectedUserService connectedUserService;
  private final ConsentPassportDAO consentPassportDAO;

  public Job consent(Integer contractId) {
    return consentPassportDAO.consent(ConsentPassportDTO.builder()
        .contractId(contractId)
        .vehicleOwnerUserWallet(connectedUserService.getWallet())
        .build()
    );
  }
}
