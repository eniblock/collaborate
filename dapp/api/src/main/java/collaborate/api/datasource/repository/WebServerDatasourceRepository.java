package collaborate.api.datasource.repository;

import collaborate.api.datasource.domain.web.WebServerDatasource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebServerDatasourceRepository extends JpaRepository <WebServerDatasource,Long>{
}
