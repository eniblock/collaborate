package collaborate.api.datasource.gateway.traefik.model;

import collaborate.api.datasource.gateway.traefik.model.middleware.Middleware;
import collaborate.api.datasource.gateway.traefik.model.servertransport.ServersTransport;
import collaborate.api.datasource.gateway.traefik.model.service.Server;
import collaborate.api.datasource.gateway.traefik.model.service.Service;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  @JsonIgnore
  public Optional<String> findFirstServiceLoadBalancerUri() {
    return services.values().stream()
        .map(service -> service.getLoadBalancer().getServers())
        .flatMap(List::stream)
        .map(Server::getUrl)
        .findFirst();
  }
}
