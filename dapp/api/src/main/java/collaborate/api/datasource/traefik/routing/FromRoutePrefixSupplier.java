package collaborate.api.datasource.traefik.routing;

import collaborate.api.datasource.domain.web.WebServerResource;
import java.util.function.Supplier;

public class FromRoutePrefixSupplier implements Supplier<String> {

  private final String routePrefix;

  public FromRoutePrefixSupplier(String datasourceName, WebServerResource resource){
      this.routePrefix = "/datasource/" + datasourceName + "/"
          + new RoutingKeyKeywordSupplier(resource.getKeywords()).get();
  }

  @Override
  public String get() {
    return routePrefix;
  }
}
