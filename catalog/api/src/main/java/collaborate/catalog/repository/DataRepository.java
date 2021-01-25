package collaborate.catalog.repository;

import collaborate.catalog.domain.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DataRepository extends MongoRepository<Data, String> {
    List<Data> deleteByOrganizationNameAndDatasourceId(String organizationName, Long datasourceId);
    Page<Data> findByOrganizationNameAndDatasourceId(String organizationName, Long datasourceId, Pageable pageable);
}
