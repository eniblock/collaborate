package collaborate.api.datasource.gateway.traefik.routing;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.TEST_CONNECTION;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Supply the keyworld used as an identifier for providers fields
 */
public class RoutingKeyFromKeywordSupplier implements Supplier<String> {

  public static final String SCOPE_PREFIX = "scope:";
  public static final Set<String> ROUTING_KEY_PREFIXES = Set.of(SCOPE_PREFIX);

  private final String routingKey;

  public RoutingKeyFromKeywordSupplier(Collection<String> keywords) {
    var assetList = keywords.stream()
        .filter(TEST_CONNECTION::equals)
        .findFirst();

    if (assetList.isPresent()) {
      routingKey = assetList.get();
    } else {
      var matchingPrefix = getMatchingPrefix(keywords)
          .orElseThrow(() ->
              new IllegalStateException(
                  String.format("no %s keyword or keyword beginning with %s", TEST_CONNECTION,
                      SCOPE_PREFIX)
              )
          );
      routingKey = getRoutingKey(keywords, matchingPrefix);
    }
  }

  private Optional<String> getMatchingPrefix(Collection<String> keywords) {
    return ROUTING_KEY_PREFIXES.stream()
        .filter(prefix -> keywords.stream().anyMatch(k -> k.startsWith(prefix)))
        .findFirst();
  }

  private String getRoutingKey(Collection<String> keywords, String matchingPrefix) {
    return keywords.stream()
        .filter(k -> k.startsWith(matchingPrefix))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("no keyword beginning by \"" + SCOPE_PREFIX + "\""));
  }

  @Override
  public String get() {
    return routingKey;
  }
}
