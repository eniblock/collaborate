package collaborate.catalog.controller;

import collaborate.catalog.domain.Document;
import collaborate.catalog.repository.DocumentRepository;
import org.keycloak.KeycloakPrincipal;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DocumentController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TopicExchange topic;

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    @PreAuthorize("principal.getKeycloakSecurityContext().getToken().getIssuedFor() == #organizationId")
    public ResponseEntity<Document> add(@AuthenticationPrincipal KeycloakPrincipal principal, @PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @RequestBody Document document) {
        // TODO data validation (organization, datasource, etc)

        rabbitTemplate.convertAndSend(
                topic.getName(),
                "document.create",
                document
        );

        return ResponseEntity.ok(document);
    }

    @DeleteMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    @PreAuthorize("principal.getKeycloakSecurityContext().getToken().getIssuedFor() == #organizationId")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@AuthenticationPrincipal KeycloakPrincipal principal, @PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId) {
        documentRepository.deleteByOrganizationIdAndDatasourceId(organizationId, datasourceId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    public Page<Document> list(@AuthenticationPrincipal KeycloakPrincipal principal, @PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, Pageable pageable) {
        Page<Document> documentPage = documentRepository.findByOrganizationIdAndDatasourceId(organizationId, datasourceId, pageable);

        return documentPage;
    }

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}/documents")
    public Page<Document> listByScope(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId, Pageable pageable, @RequestParam(required = false) String q) {
        Page<Document> documentPage = documentRepository.findByOrganizationIdAndDatasourceIdAndScopeIdAndTitleIgnoreCaseLike(organizationId, datasourceId, scopeId, pageable, q);

        return documentPage;
    }
}
