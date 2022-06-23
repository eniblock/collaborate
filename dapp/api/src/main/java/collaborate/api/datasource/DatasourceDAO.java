package collaborate.api.datasource;

import collaborate.api.datasource.model.Datasource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasourceDAO extends JpaRepository<Datasource, String> {

}
