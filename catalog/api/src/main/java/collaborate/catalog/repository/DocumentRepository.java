package collaborate.catalog.repository;

import collaborate.catalog.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentRepository extends MongoRepository<Document, String> {
    List<Document> deleteByOrganizationIdAndDatasourceId(String organizationId, Long datasourceId);
    Page<Document> findByOrganizationIdAndDatasourceId(String organizationId, Long datasourceId, Pageable pageable);
}
