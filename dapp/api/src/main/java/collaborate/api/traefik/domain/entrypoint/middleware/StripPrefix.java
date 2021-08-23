package collaborate.api.traefik.domain.entrypoint.middleware;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Removing Prefixes From the Path Before Forwarding the Request
 * <br>
 * Remove the specified prefixes from the URL path.
 *
 * @see <a href="https://doc.traefik.io/traefik/middlewares/stripprefix/">Traefik StripPrefix
 * middleware doc</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripPrefix {

  private List<String> prefixes;
}
