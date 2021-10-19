package collaborate.api.datasource.model.traefik.middleware;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The AddPrefix middleware updates the path of a request before forwarding it.
 *
 * @see <a href="https://doc.traefik.io/traefik/middlewares/addprefix/">Traefik AddPrefix middleware
 * doc</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPrefix {

  private String prefix;

}
