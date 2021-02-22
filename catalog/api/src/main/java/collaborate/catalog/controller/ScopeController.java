package collaborate.catalog.controller;

import collaborate.catalog.domain.Scope;
import collaborate.catalog.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ScopeController {

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("scopes")
    public List<Scope> list() {
        List<Scope> scopes = documentRepository.findScopes();

        return scopes;
    }

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}")
    public Scope get(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId) {
        Scope scope = documentRepository.findScope(organizationId, datasourceId, scopeId);

        return scope;
    }

    @GetMapping("scopes/{id}")
    public Scope getScopeById(@PathVariable(value="id") UUID scopeId) {
        return documentRepository.findScopeById(scopeId);
    }
}
