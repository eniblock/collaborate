package collaborate.api.datasource.servicedata.access.model;

import static collaborate.api.datasource.servicedata.access.model.AccessRequestParams.AttributeName.PROVIDER_ADDRESS;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @JsonProperty(PROVIDER_ADDRESS)
  @NotEmpty
  private String providerAddress;

}
