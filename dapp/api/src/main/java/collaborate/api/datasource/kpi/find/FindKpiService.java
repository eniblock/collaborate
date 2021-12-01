package collaborate.api.datasource.kpi.find;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindKpiService {

  private final FindKpiCustomDAO findKpiCustomDAO;

  public Collection<KpiAggregation> find(KpiQuery kpiQuery) {
    return findKpiCustomDAO.find(kpiQuery);
  }

}
