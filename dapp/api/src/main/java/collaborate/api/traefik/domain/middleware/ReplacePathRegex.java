package collaborate.api.traefik.domain.middleware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplacePathRegex {

  private String regex;
  private String replacement;
}
