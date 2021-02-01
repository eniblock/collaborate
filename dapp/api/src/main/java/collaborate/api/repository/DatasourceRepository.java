package collaborate.api.repository;

import collaborate.api.domain.Datasource;
import collaborate.api.domain.enumeration.DatasourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, Long> {
    List<Datasource> findByStatus(DatasourceStatus status);
}
