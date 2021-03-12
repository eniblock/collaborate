package collaborate.catalog.controller;

import collaborate.catalog.domain.Scope;
import collaborate.catalog.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ScopeController {

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("scopes")
    public List<Scope> list(@RequestParam("sortingFields") String[] sortingFields) {
        List<Scope> scopes = documentRepository.findScopes(sortingFields);

        return scopes;
    }

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}")
    public Scope get(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId) {
        List<Scope> scopes = documentRepository.findScopes(organizationId, datasourceId, scopeId);

        if (scopes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return scopes.get(0);
    }

    @GetMapping("scopes/{id}")
    public Scope getScopeById(@PathVariable(value="id") UUID scopeId) {
        return documentRepository.findScopeById(scopeId);
    }
}
