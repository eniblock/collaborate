package collaborate.catalog.repository;

import collaborate.catalog.domain.Scope;

import java.util.List;
import java.util.UUID;

public interface ScopeRepository {
    public List<Scope> findScopes();
    public List<Scope> findScopes(String organizationId, Long datasourceId, UUID scopeId);
    public Scope findScopeById(UUID scopeId);
}
