package collaborate.catalog.repository;

import collaborate.catalog.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends MongoRepository<Document, String>, ScopeRepository {
    List<Document> deleteByOrganizationIdAndDatasourceId(String organizationId, Long datasourceId);

    Page<Document> findByOrganizationIdAndDatasourceId(String organizationId, Long datasourceId, Pageable pageable);

    Page<Document> findByOrganizationIdAndDatasourceIdAndScopeId(String organizationId, Long datasourceId, UUID scopeId, Pageable pageable);

    Document findOneByOrganizationIdAndDatasourceIdAndScopeId(String organizationId, Long datasourceId, UUID scopeId);

    Page<Document> findByOrganizationIdAndDatasourceIdAndScopeIdAndTitleIgnoreCaseLike(String organizationId, Long datasourceId, UUID scopeId, Pageable pageable, String q);
}
