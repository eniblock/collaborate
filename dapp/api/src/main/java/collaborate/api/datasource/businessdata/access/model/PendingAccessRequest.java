package collaborate.api.datasource.businessdata.access.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "pending_access_request")
@ToString
public class PendingAccessRequest {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Id implements Serializable {

    String requester;
    Integer nftId;
  }

  @EmbeddedId
  private Id id;

  public PendingAccessRequest(String requester, Integer nftId) {
    this(new Id(requester, nftId));
  }

}
