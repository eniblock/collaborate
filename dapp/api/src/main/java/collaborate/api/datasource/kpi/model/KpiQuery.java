package collaborate.api.datasource.kpi.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiQuery {

  // TODO Validate
  private String datetimeFormat;
  private String dataSetsGroup;
  private String labelGroup;
  private Set<SearchCriteria> search;

}
