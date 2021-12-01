package collaborate.api.datasource.kpi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KpiService {

  private final KpiDAO kpiDAO;

  public void save(Kpi kpi) {
    kpiDAO.save(kpi);
  }

  public void saveIfValueMissing(Kpi kpi, KpiSpecification onMissingCondition) {
    if (kpiDAO.findAll(onMissingCondition).isEmpty()) {
      kpiDAO.save(kpi);
    } else {
      log.info("Kpi already exist with value having {}", onMissingCondition);
    }
  }
}
