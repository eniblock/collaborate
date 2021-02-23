package collaborate.catalog.controller;

import collaborate.catalog.domain.Scope;
import collaborate.catalog.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("scopes")
public class ScopeController {

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping()
    public List<Scope> list() {
        List<Scope> scopes = documentRepository.findScopes();

        return scopes;
    }

    @GetMapping("{id}")
    public Scope getScopeById(@PathVariable(value="id") UUID scopeId) {
        return documentRepository.findScopeById(scopeId);
    }
}