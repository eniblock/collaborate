package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.datasource.model.traefik.Router;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RouterFactory {

  public Router create(String routePrefix, String serviceName,
      List<String> middlewareNames, boolean tls){
    return Router.builder()
        .entryPoints(List.of("websecure"))
        .rule("PathPrefix(`" + routePrefix + "`)")
        .service(serviceName)
        .middlewares(middlewareNames)
        .tls(tls)
        .build();
  }
}
