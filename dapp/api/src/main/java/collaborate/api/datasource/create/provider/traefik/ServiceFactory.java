package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.datasource.model.traefik.service.LoadBalancer;
import collaborate.api.datasource.model.traefik.service.Server;
import collaborate.api.datasource.model.traefik.service.Service;
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
