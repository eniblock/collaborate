package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.Organization;
import collaborate.api.domain.Scope;
import collaborate.api.domain.enumeration.ScopeStatus;
import collaborate.api.repository.AccessRequestRepository;
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

import java.util.Optional;

@RestController
@RequestMapping("scopes")
public class ScopeController {

    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    @GetMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<Page<Scope>> list() {
        Page<Scope> scopes = catalogClient.getScopes();

        for (Scope s : scopes) {
            Optional<Organization> optionalProvider = apiProperties.findOrganizationWithOrganizationId(s.getOrganizationId());

            if (!optionalProvider.isPresent()) {
                continue;
            }

            Organization provider = optionalProvider.get();
            Organization requester = apiProperties.getOrganizations().get(apiProperties.getOrganizationPublicKeyHash());

            AccessRequest accessRequest =
                    accessRequestRepository
                            .findFirstByProviderAddressAndRequesterAddressAndDatasourceIdAndScopeIdOrderByCreatedAtDesc(
                                    provider.getPublicKeyHash(),
                                    requester.getPublicKeyHash(),
                                    s.getDatasourceId(),
                                    s.getScopeId()
                            );

            if (accessRequest != null) {
                switch (accessRequest.getStatus()) {
                    case REQUESTED:
                        s.setStatus(ScopeStatus.PENDING);
                        break;
                    case REVOKED:
                    case REJECTED:
                        s.setStatus(ScopeStatus.LOCKED);
                        break;
                    case GRANTED:
                        s.setStatus(ScopeStatus.GRANTED);
                        break;
                }
            } else {
                s.setStatus(ScopeStatus.LOCKED);
            }
        }

        return ResponseEntity.ok(scopes);
    }
}
