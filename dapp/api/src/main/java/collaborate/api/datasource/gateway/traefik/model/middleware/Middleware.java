package collaborate.api.datasource.gateway.traefik.model.middleware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class Middleware {

  private Headers headers;
  private AddPrefix addPrefix;
  private RedirectRegex redirectRegex;
  private ReplacePathRegex replacePathRegex;
  private StripPrefix stripPrefix;
}
