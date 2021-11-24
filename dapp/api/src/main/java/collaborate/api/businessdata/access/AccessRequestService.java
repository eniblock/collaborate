package collaborate.api.businessdata.access;

import collaborate.api.businessdata.access.model.AccessRequestDTO;
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

  public Job requestAccess(List<AccessRequestDTO> accessRequestDTOs) {
    return accessRequestDAO.accessRequest(accessRequestDTOs);
  }
}
