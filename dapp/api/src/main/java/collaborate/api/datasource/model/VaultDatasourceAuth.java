package collaborate.api.datasource.model;

import collaborate.api.datasource.model.dto.web.authentication.Authentication;
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
public class VaultDatasourceAuth {

  private Authentication authentication;
  private String jwt;

  @JsonIgnore
  public boolean hasJwt() {
    return jwt != null;
  }
}
