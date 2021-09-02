package collaborate.api.traefik.domain.middleware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Redirecting the Client to a Different Location<br>
 * The RedirectRegex redirects a request using regex matching and replacement.
 *
 * @see <a href="https://doc.traefik.io/traefik/middlewares/redirectregex/">Traefik RedirectRegex
 * middleware doc</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RedirectRegex {

  /**
   * Set the permanent option to true to apply a permanent redirection.
   */
  private Boolean permanent;
  /**
   * The regex option is the regular expression to match and capture elements from the request URL.
   */
  private String regex;
  /**
   * The replacement option defines how to modify the URL to have the new target URL.
   */
  private String replacement;
}
