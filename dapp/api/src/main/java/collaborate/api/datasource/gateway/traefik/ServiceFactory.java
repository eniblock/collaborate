package collaborate.api.datasource.gateway.traefik;

import collaborate.api.datasource.gateway.traefik.model.service.LoadBalancer;
import collaborate.api.datasource.gateway.traefik.model.service.Server;
import collaborate.api.datasource.gateway.traefik.model.service.Service;
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
