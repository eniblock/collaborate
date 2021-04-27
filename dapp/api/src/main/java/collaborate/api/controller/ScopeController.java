package collaborate.api.controller;

import collaborate.api.comparator.ScopeComparator;
import collaborate.api.config.OpenApiConfig;
import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.Scope;
import collaborate.api.repository.AccessRequestRepository;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.ScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
public class ScopeController {

    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private ScopeService scopeService;

    @GetMapping("scopes")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<List<Scope>> list() {
        String[] sortingFields = new String[] {"organizationId", "scope"};
        List<Scope> scopes = catalogClient.getScopes(sortingFields);

        for (Scope scope : scopes) {
            scope.setStatusFromAccessRequest(scopeService.getAccessRequest(scope));
        }

        Collections.sort(scopes, new ScopeComparator.StatusSorter());

        return ResponseEntity.ok(scopes);
    }

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<Scope> get(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId) {
        Scope scope = catalogClient.getScope(organizationId, datasourceId, scopeId);

        scope.setStatusFromAccessRequest(scopeService.getAccessRequest(scope));

        return ResponseEntity.ok(scope);
    }
}
