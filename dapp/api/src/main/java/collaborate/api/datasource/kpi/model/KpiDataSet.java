package collaborate.api.datasource.kpi.model;

import collaborate.api.organization.tag.Organization;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiDataSet {

  private Organization organizationWallet;
  private List<Long> data;
}
