package collaborate.api.datasource.traefik.routing;

import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Supply the keyworld used as an identifier for providers fields
 */
public class RoutingKeyFromKeywordSupplier implements Supplier<String> {

  public static final String SCOPE_PREFIX = "scope:";
  public static final String PURPOSE_PREFIX = "purpose:";
  public static final Set<String> ROUTING_KEY_PREFIXES = Set.of(SCOPE_PREFIX, PURPOSE_PREFIX);

  private final String routingKey;

  public RoutingKeyFromKeywordSupplier(Collection<String> keywords) {
    var matchingPrefix = getMatchingPrefix(keywords);
    routingKey = getRoutingKey(keywords, matchingPrefix);
  }

  private String getMatchingPrefix(Collection<String> keywords) {
    return ROUTING_KEY_PREFIXES.stream()
        .filter(prefix -> keywords.stream().anyMatch(k -> k.startsWith(prefix)))
        .findFirst().orElseThrow(() ->
            new IllegalStateException(
                "no keyword beginning with one of the following prefixes: ["
                    + ROUTING_KEY_PREFIXES.stream().collect(joining(",", "\"", "\""))
                    + "]"
            )
        );
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
