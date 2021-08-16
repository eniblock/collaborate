package collaborate.api.datasource.repository;

import collaborate.api.datasource.domain.DataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

    @Query("select d from DataSource d where UPPER(d.name) like UPPER(CONCAT('%', :name, '%'))")
    Page<DataSource> findByNameIgnoreCaseLike(Pageable pageable, String name);

}
