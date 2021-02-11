package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.Scope;
import collaborate.api.domain.enumeration.ScopeStatus;
import collaborate.api.restclient.ICatalogClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("scopes")
public class ScopeController {

    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private ICatalogClient catalogClient;

    @GetMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<Page<Scope>> list() {
        Page<Scope> scopes = catalogClient.getScopes();

        for (Scope s : scopes) {
            s.setStatus(ScopeStatus.LOCKED);
        }

        return ResponseEntity.ok(scopes);
    }
}
