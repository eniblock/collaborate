package collaborate.api.traefik.domain.servertransport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ServersTransport allows to configure the transport between Traefik and your servers.
 *
 * @see <a href="https://doc.traefik.io/traefik/routing/services/#serverstransport_1">Traefik
 * ServersTransport doc</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ServersTransport {

  /**
   * Optional<br> configure the server name that will be used for SNI.
   */
  private String serverName;

  /**
   * Optional<br> is the list of certificates (as file paths, or data bytes) that will be set as
   * client certificates for mTLS.
   */
  private List<Certificates> certificates;
  /**
   * Optional<br> disables SSL certificate verification.
   */
  private Boolean insecureSkipVerify;
  /**
   * Optional<br> is the list of certificates (as file paths, or data bytes) that will be set as
   * Root Certificate Authorities when using a self-signed TLS certificate.
   */
  private List<String> rootCAs;
  /**
   * Optional, Default=2<<br> If non-zero, maxIdleConnsPerHost controls the maximum idle
   * (keep-alive) connections to keep per-host.
   */
  private Integer maxIdleConnsPerHost;
  /**
   * Optional<<br> is about a number of timeouts relevant to when forwarding requests to the backend
   * servers.
   */
  private ForwardingTimeouts forwardingTimeouts;
}
