package collaborate.api.datasource.servicedata.access;

import collaborate.api.datasource.servicedata.access.model.AccessRequestDTO;
import collaborate.api.datasource.servicedata.access.model.AccessRequestParams;
import collaborate.api.tag.model.job.Job;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubscribeRequestService {

  private final RequestSubscribeDAO accessRequestDAO;

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
