package collaborate.api.traefik.domain.servertransport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ForwardingTimeouts is about a number of timeouts relevant to when forwarding requests to the
 * backend servers.
 *
 * @see <a href="https://doc.traefik.io/traefik/routing/services/#forwardingtimeouts">Traefik
 * ForwardingTimeouts doc</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ForwardingTimeouts {

  /**
   * Optional, Default=30s<br> is the maximum duration allowed for a connection to a backend server
   * to be established. Zero means no timeout
   */
  private String dialTimeout;
  /**
   * Optional, Default=0s<br> if non-zero, specifies the amount of time to wait for a server's
   * response headers after fully writing the request (including its body, if any). This time does
   * not include the time to read the response body. Zero means no timeout.
   */
  private String responseHeaderTimeout;
  /**
   * Optional, Default=90s<br> is the maximum amount of time an idle (keep-alive) connection will
   * remain idle before closing itself. Zero means no limit.
   */
  private String idleConnTimeout;
}
