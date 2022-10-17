package collaborate.api.datasource.servicedata.consent;

import collaborate.api.tag.model.job.Job;
import collaborate.api.user.connected.ConnectedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsentServiceDataService {

  private final ConnectedUserService connectedUserService;
  private final ConsentServiceDataDAO consentServiceDataDAO;

  public Job consent(Integer contractId) {
    return consentServiceDataDAO.consent(ConsentServiceDataDTO.builder()
        .contractId(contractId)
        .vehicleOwnerUserWallet(connectedUserService.getWallet())
        .build()
    );
  }
}
