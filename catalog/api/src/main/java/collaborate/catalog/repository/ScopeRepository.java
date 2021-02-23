package collaborate.catalog.repository;

import collaborate.catalog.domain.Scope;

import java.util.List;
import java.util.UUID;

public interface ScopeRepository {
    public List<Scope> findScopes();
    public Scope findScopeById(UUID scopeId);
}