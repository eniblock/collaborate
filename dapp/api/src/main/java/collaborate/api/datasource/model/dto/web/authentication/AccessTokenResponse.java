package collaborate.api.datasource.model.dto.web.authentication;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccessTokenResponse {

  @NotNull
  private String accessToken;
  private Integer expiresIn;
  private String refreshToken;
  private String tokenType;
  private String scope;

}
