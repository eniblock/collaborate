package collaborate.api.datasource.kpi;

import collaborate.api.datasource.kpi.model.KpiAggregation;
import collaborate.api.datasource.kpi.model.KpiQuery;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpiService {

  private final KpiCustomDAO kpiCustomDAO;

  public Collection<KpiAggregation> find(KpiQuery kpiQuery) {
    return kpiCustomDAO.search(kpiQuery);
  }

}
