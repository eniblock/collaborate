package collaborate.catalog.repository;

import collaborate.catalog.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SearchRepository {
    Page<Document> searchByScope(String organizationId, Long datasourceId, UUID scopeId, Pageable pageable, String q);
}
