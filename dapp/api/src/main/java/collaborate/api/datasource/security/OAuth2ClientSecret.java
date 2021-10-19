package collaborate.api.datasource.security;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2ClientSecret implements Serializable {

  private String clientId;
  private String clientSecret;
}
