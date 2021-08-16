package collaborate.api.traefik.mapping;

import collaborate.api.traefik.domain.entrypoint.service.LoadBalancer;
import collaborate.api.traefik.domain.entrypoint.service.Server;
import collaborate.api.traefik.domain.entrypoint.service.Service;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ServiceFactory {

  public Service create(String baseUrl, String serverTransportName) {
    return Service.builder()
        .loadBalancer(LoadBalancer.builder()
            .servers(List.of(new Server(baseUrl))).passHostHeader(false)
            .serversTransport(serverTransportName)
            .build()
        ).build();
  }
}
