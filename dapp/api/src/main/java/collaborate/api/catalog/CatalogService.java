package collaborate.api.catalog;

import collaborate.api.domain.Document;
import collaborate.api.domain.Scope;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CatalogService {

  public Page<Document> getDocumentsByScope(String organizationId, Long datasourceId, UUID scopeId,
      Pageable pageable, String q) {
    throw new NotImplementedException();
  }

  public Page<Document> getDocuments(Pageable pageable, String q) {
    throw new NotImplementedException();
  }

  public List<Scope> getScopes(String[] sortingFields) {
    throw new NotImplementedException();
  }

  public Scope getScope(String organizationId, Long datasourceId, UUID scopeId) {
    throw new NotImplementedException();
  }

  public Page<Document> get(String organizationId, Long id) {
    throw new NotImplementedException();
  }

  public Document getDocumentById(String documentId) {
    throw new NotImplementedException();
  }

  public Scope getScopeById(UUID scopeId) {
    throw new NotImplementedException();
  }
}
