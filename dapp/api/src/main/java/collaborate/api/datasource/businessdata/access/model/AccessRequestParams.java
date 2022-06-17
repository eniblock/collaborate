package collaborate.api.datasource.businessdata.access.model;

import static collaborate.api.datasource.businessdata.access.model.AccessRequestParams.AttributeName.PROVIDER_ADDRESS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessRequestParams implements Serializable {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class AttributeName {

    public static final String PROVIDER_ADDRESS = "provider_address";
  }

  @NotNull
  private Integer nftId;


  /**
   * @deprecated see:
   * <a
   * href="https://xdevtechnologies.atlassian.net/wiki/spaces/DA/pages/667549717/Request+access+no+storage+required">...</a>
   */
  @Deprecated(forRemoval = true)
  @NotEmpty
  private List<String> scopes;

  @JsonProperty(PROVIDER_ADDRESS)
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
