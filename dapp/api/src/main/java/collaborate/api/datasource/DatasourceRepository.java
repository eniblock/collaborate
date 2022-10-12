package collaborate.api.datasource;

import collaborate.api.datasource.model.Datasource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, String> {

  Page<Datasource> findAllByOwner(String owner, Pageable pageable);
}
