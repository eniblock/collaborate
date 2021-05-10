package collaborate.api.repository;

import collaborate.api.domain.Datasource;
import collaborate.api.domain.enumeration.DatasourceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, Long> {
    List<Datasource> findByStatus(DatasourceStatus status);

    @Query("select d from Datasource d where UPPER(d.name) like UPPER(CONCAT('%', :name, '%'))")
    Page<Datasource> findByNameIgnoreCaseLike(Pageable pageable, String name);
}
