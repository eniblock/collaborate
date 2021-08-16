package collaborate.api.traefik.domain.entrypoint;

import collaborate.api.traefik.domain.entrypoint.middleware.Middleware;
import collaborate.api.traefik.domain.entrypoint.servertransport.ServersTransport;
import collaborate.api.traefik.domain.entrypoint.service.Service;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class Http {

  private Map<String, Router> routers = new HashMap<>();
  private Map<String, Service> services = new HashMap<>();
  private Map<String, Middleware> middlewares = new HashMap<>();
  private Map<String, ServersTransport> serversTransports = new HashMap<>();
}
