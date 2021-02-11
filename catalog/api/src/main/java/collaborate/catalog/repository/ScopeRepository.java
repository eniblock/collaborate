package collaborate.catalog.repository;

import collaborate.catalog.domain.Scope;

import java.util.List;

public interface ScopeRepository {
    public List<Scope> findScopes();
}
