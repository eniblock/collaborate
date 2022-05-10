package collaborate.api.datasource.gateway.traefik.routing;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_SCOPE;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_TEST_CONNECTION;

import collaborate.api.datasource.model.dto.web.Attribute;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Supply the keyworld used as an identifier for providers fields
 */
public class RoutingKeyFromKeywordSupplier implements Supplier<String> {

  public static final String ATTR_NAME_ALIAS = "provider:routing:alias";

  private final String routingKey;

  public RoutingKeyFromKeywordSupplier(Collection<Attribute> keywords) {
    var assetList = Attribute.findFirstByName(keywords, ATTR_NAME_TEST_CONNECTION);
    // Resource with "asset-list" attribute name use always the "asset-list" routing key
    if (assetList.isPresent()) {
      routingKey = ATTR_NAME_TEST_CONNECTION;
    } else {
      var optAlias = Attribute.findFirstByName(keywords, ATTR_NAME_ALIAS);
      routingKey = optAlias.map(Attribute::getValue).orElseThrow(() ->
          new IllegalStateException(
              String.format("no attribute named '%s' or attribute named '%s'",
                  ATTR_NAME_TEST_CONNECTION,
                  ATTR_NAME_ALIAS
              )
          ));
    }
  }

  @Override
  public String get() {
    return routingKey;
  }
}
