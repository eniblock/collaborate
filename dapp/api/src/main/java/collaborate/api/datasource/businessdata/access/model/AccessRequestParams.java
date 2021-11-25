package collaborate.api.datasource.businessdata.access.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccessRequestParams implements Serializable {

  @NotNull
  private Integer nftId;
  @NotEmpty
  private List<String> scopes;
  @NotEmpty
  private String providerAddress;
  @NotNull
  private UUID accessRequestsUuid;

  @JsonIgnore
  public String getDatasourceId() {
    return StringUtils.substringBefore(getScopes().get(0), ":");
  }

  @JsonIgnore
  public String getDatasourceScope() {
    return StringUtils.substringAfter(getScopes().get(0), ":");
  }
}
