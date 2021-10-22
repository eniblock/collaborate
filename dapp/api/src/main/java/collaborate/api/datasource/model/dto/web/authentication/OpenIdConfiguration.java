package collaborate.api.datasource.model.dto.web.authentication;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.net.URI;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OpenIdConfiguration {

  private URI issuer;
  private URI authorizationEndpoint;
  @NotNull
  private URI tokenEndpoint;
  private String[] scopesSupported;
  private String[] responseTypesSupported;

}
