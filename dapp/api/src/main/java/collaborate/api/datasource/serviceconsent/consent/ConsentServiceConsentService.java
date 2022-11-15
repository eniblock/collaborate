package collaborate.api.datasource.serviceconsent.consent;

import collaborate.api.tag.model.job.Job;
import collaborate.api.user.connected.ConnectedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsentServiceConsentService {

  private final ConnectedUserService connectedUserService;
  private final ConsentServiceConsentDAO consentServiceConsentDAO;

  public Job consent(Integer contractId) {
    return consentServiceConsentDAO.consent(ConsentServiceConsentDTO.builder()
        .contractId(contractId)
        .vehicleOwnerUserWallet(connectedUserService.getWallet())
        .build()
    );
  }
}
