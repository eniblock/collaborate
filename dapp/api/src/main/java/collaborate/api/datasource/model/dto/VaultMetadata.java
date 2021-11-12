package collaborate.api.datasource.model.dto;

import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.datasource.security.BasicAuthCredentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class VaultMetadata {

  private BasicAuthCredentials basicAuthCredentials;
  private OAuth2 oAuth2;
  private String jwt;

  @JsonIgnore
  public boolean hasOAuth2() {
    return oAuth2 != null;
  }

  @JsonIgnore
  public boolean hasJwt() {
    return jwt != null;
  }
}
