package collaborate.api.datasource.kpi.model;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiResult {

  private String kpiKey;
  private Collection<String> labels;
  private List<KpiDataSet> dataSets;

}
