package collaborate.api.datasource.kpi.find;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KpiAggregation {

  private String dataSetsGroup;
  private String label;
  private Long total;

}
