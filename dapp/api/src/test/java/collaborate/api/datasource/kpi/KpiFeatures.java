package collaborate.api.datasource.kpi;

import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class KpiFeatures {

  public static List<Kpi> kpis = TestResources.readContent(
      "/datasource/kpi/kpis.json",
      new TypeReference<>() {
      });

}
