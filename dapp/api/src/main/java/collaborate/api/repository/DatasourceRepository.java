package collaborate.api.repository;

import collaborate.api.domain.Datasource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasourceRepository extends JpaRepository<Datasource, Long> {
}
