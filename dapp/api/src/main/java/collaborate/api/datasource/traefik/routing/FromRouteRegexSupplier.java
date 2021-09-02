package collaborate.api.datasource.traefik.routing;

import collaborate.api.datasource.domain.web.WebServerResource;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Supply the unified route prefix regex
 */
public class FromRouteRegexSupplier implements Supplier<String> {

  private final String route;
  private static final Pattern pathParamPattern = Pattern.compile("\\$[0-9]+");

  public FromRouteRegexSupplier(String datasourceName, WebServerResource resource) {
    var routeBuilder = new StringBuilder(
        new FromRoutePrefixSupplier(datasourceName, resource).get());

    var matcher = pathParamPattern.matcher(resource.getUrl());
    var matches = 0;
    while (matcher.find()) {
      matches++;
    }
    routeBuilder.append("/(.*)".repeat(Math.max(0, matches)));

    this.route = routeBuilder.toString();
  }

  @Override
  public String get() {
    return route;
  }
}
