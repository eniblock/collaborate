package collaborate.api.datasource.model.traefik.middleware;

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
