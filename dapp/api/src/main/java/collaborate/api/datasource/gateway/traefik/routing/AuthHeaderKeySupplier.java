package collaborate.api.datasource.gateway.traefik.routing;

import java.util.function.Supplier;

public class AuthHeaderKeySupplier implements Supplier<String> {

  private final String name;

  public AuthHeaderKeySupplier(DatasourceKeySupplier datasourceKeySupplier) {
    this.name = datasourceKeySupplier.get() + "-auth-headers";
  }

  @Override
  public String get() {
    return name;
  }

}
