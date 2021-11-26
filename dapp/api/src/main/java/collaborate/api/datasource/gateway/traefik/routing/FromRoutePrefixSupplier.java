package collaborate.api.datasource.gateway.traefik.routing;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Build the Data API Gateway router "PathPrefix" value: ex:
 * <code>/datasource/{{datasourceId}}/scope/odometer</code>
 */
public class FromRoutePrefixSupplier implements Supplier<String> {

  private final String routePrefix;

  public FromRoutePrefixSupplier(String datasourceName, Collection<String> keywords) {
    this.routePrefix = "/datasource/" + datasourceName + "/"
        + new RoutingKeyFromKeywordSupplier(keywords).get();
  }

  @Override
  public String get() {
    return routePrefix;
  }
}
