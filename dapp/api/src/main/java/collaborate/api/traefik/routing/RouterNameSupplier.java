package collaborate.api.traefik.routing;

import collaborate.api.datasource.domain.web.WebServerResource;
import java.util.function.Supplier;

public class RouterNameSupplier implements Supplier<String> {

  private final String name;

  public RouterNameSupplier(String datasourceName, WebServerResource resource) {
    this.name = datasourceName + "-" + new RoutingKeyKeywordSupplier(resource.getKeywords()).get();
  }

  @Override
  public String get() {
    return name;
  }
}
