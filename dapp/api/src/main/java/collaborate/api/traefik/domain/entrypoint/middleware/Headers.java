package collaborate.api.traefik.domain.entrypoint.middleware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Headers middleware manages the headers of requests and responses.
 *
 * @see <a href="https://doc.traefik.io/traefik/middlewares/headers/">Traefik Headers middleware
 * doc</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Headers {

  /**
   * The customRequestHeaders option lists the header names and values to apply to the request.
   */
  private Map<String, String> customRequestHeaders;
  /**
   * The customResponseHeaders option lists the header names and values to apply to the response.
   */
  private Map<String, String> customResponseHeaders;
}
