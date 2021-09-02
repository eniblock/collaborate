package collaborate.api.datasource.traefik.routing;

import collaborate.api.datasource.domain.Datasource;
import java.util.function.Supplier;

public class DatasourceNameSupplier implements Supplier<String> {

  private final String name;

  public DatasourceNameSupplier(Datasource datasource) {
      this.name = datasource.getId().toString();
  }

  @Override
  public String get() {
    return name;
  }
}
