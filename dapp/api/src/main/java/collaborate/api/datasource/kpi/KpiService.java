package collaborate.api.datasource.kpi;

import collaborate.api.datasource.kpi.find.FindKpiCustomDAO;
import collaborate.api.datasource.kpi.find.KpiAggregation;
import collaborate.api.datasource.kpi.find.KpiQuery;
import collaborate.api.datasource.kpi.find.SearchCriteria;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KpiService {

  private final KpiDAO kpiDAO;
  private final FindKpiCustomDAO findKpiCustomDAO;

  public void save(Kpi kpi) {
    kpiDAO.save(kpi);
  }

  public Long count(KpiSpecification kpiSpecification) {
    return kpiDAO.count(kpiSpecification);
  }

  public List<Kpi> find(KpiSpecification criteria) {
    return kpiDAO.findAll(criteria);
  }

  public Collection<KpiAggregation> find(KpiQuery kpiQuery) {
    return findKpiCustomDAO.find(kpiQuery);
  }

  public Collection<Kpi> find(Collection<SearchCriteria> searchCriteria) {
    return findKpiCustomDAO.find(searchCriteria);
  }

  public void saveIfValueMissing(Kpi kpi, KpiSpecification missingCriteria) {
    if (kpiDAO.findAll(missingCriteria).isEmpty()) {
      kpiDAO.save(kpi);
    } else {
      log.info("Kpi already exists with value having {}", missingCriteria);
    }
  }

}
