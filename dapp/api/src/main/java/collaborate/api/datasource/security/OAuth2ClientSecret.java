package collaborate.api.datasource.security;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2ClientSecret implements Serializable {

  @Id
  private Long id;
  private String clientId;
  private String clientSecret;
}
