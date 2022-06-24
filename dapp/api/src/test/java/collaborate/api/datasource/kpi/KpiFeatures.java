package collaborate.api.datasource.kpi;

import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KpiFeatures {

  public static List<Kpi> kpis = TestResources.readContent(
      "/datasource/kpi/kpis.json",
      new TypeReference<List<Kpi>>() {
      });

  public static List<Kpi> filter(Predicate<Kpi> predicate) {
    return kpis.stream()
        .filter(predicate)
        .collect(Collectors.toList());
  }
}
