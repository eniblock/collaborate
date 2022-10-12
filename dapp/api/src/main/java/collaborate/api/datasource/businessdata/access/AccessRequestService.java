package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.tag.model.job.Job;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccessRequestService {

  private final RequestAccessDAO accessRequestDAO;

  public Job requestAccess(List<AccessRequestDTO> accessRequestDTOs) {
    return accessRequestDAO.accessRequest(
        accessRequestDTOs.stream()
            .map(this::toAccessRequestParam)
            .collect(Collectors.toList())
    );
  }

  AccessRequestParams toAccessRequestParam(AccessRequestDTO accessRequestDTO) {
    return AccessRequestParams.builder()
        .nftId(accessRequestDTO.getTokenId())
        .providerAddress(accessRequestDTO.getProviderAddress())
        .build();
  }
}
