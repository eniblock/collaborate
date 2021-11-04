package collaborate.api.businessdata.access.request.model;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest {

  private UUID id;
  private Long datasourceId;
  private Integer tokenId;
  private AccessRequestStatus status = AccessRequestStatus.REQUESTED;
  private String requesterAddress;
  private String providerAddress;
  private ZonedDateTime createdAt;
  private String jwtToken;

}
