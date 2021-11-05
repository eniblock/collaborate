package collaborate.api.businessdata.access.request;

import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.organization.OrganizationService;
import collaborate.api.tag.model.job.Job;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccessRequestService {

  private final AccessRequestDAO accessRequestDAO;
  private final OrganizationService organizationService;

  public Job requestAccess(List<AssetDetailsDTO> assetDetailsDTOS) {
    var requester = organizationService.getCurrentOrganization().getAddress();
    return accessRequestDAO.accessRequest(assetDetailsDTOS, requester);
  }
}
