package collaborate.api.datasource.model.dto.web.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponse {

  @NotNull
  private String accessToken;
  private Integer expiresIn;
  private String refreshToken;
  private String tokenType;
  private String scope;

  @JsonIgnore
  public String getBearerHeaderValue() {
    return "Bearer " + accessToken;
  }

}
