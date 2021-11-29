package collaborate.api.datasource.kpi.model;

import java.util.HashSet;
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

  private String kpiKey;
  // TODO Validate
  private String datetimeFormat;
  private String dataSetsGroup;
  private Set<SearchCriteria> search;

  public void addSearch(SearchCriteria searchCriteria) {
    if (search == null) {
      search = new HashSet<>();
    }
    search.add(searchCriteria);
  }
}
