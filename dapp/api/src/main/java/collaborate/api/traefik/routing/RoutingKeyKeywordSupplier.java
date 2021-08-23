package collaborate.api.traefik.routing;

import static collaborate.api.traefik.routing.RoutingKeyKeywordValidator.KEYWORD_PREFIX;

import java.util.List;
import java.util.function.Supplier;

public class RoutingKeyKeywordSupplier implements Supplier<String> {


  private final String routingKey;

  public RoutingKeyKeywordSupplier(List<String> keywords) {
    routingKey = keywords.stream()
        .filter(k -> k.contains(KEYWORD_PREFIX))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("no keyword beginning by \"" + KEYWORD_PREFIX + "\""))
        .replace(KEYWORD_PREFIX, "");
  }

  @Override
  public String get() {
    return routingKey;
  }
}
