package collaborate.api.datasource.repository;

import collaborate.api.datasource.domain.Datasource;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceRepository extends JpaRepository<Datasource, UUID> {

  @Query("select d from Datasource d where UPPER(d.name) like UPPER(CONCAT('%', :name, '%'))")
  Page<Datasource> findByNameIgnoreCaseLike(Pageable pageable, String name);

}
