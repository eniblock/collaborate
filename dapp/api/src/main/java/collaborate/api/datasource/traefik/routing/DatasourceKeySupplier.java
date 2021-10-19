package collaborate.api.datasource.traefik.routing;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import java.util.function.Supplier;

public class DatasourceKeySupplier implements Supplier<String> {

  private final String name;

  public DatasourceKeySupplier(DatasourceDTO datasource) {
      this.name = datasource.getId().toString();
  }

  @Override
  public String get() {
    return name;
  }
}
