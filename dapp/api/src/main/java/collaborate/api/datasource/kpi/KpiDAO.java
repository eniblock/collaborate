package collaborate.api.datasource.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface KpiDAO extends JpaRepository<Kpi, Long> {

}
