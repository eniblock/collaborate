package collaborate.api.traefik.domain.entrypoint.servertransport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Certificates is the list of certificates (as file paths, or data bytes) that will be set as
 * client certificates for mTLS.
 *
 * @see <a href="https://doc.traefik.io/traefik/routing/services/#certificates">Traefik Certificates
 * doc</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class Certificates {

  private String certFile;
  private String keyFile;
}
