package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.Organization;
import collaborate.api.domain.Scope;
import collaborate.api.domain.enumeration.AccessRequestStatus;
import collaborate.api.repository.AccessRequestRepository;
import collaborate.api.repository.OrganizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("access-requests")
public class AccessRequestController {
    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public void create(@RequestBody Scope[] scopes) {
        for (Scope scope : scopes) {
            Organization provider = organizationRepository.findById(scope.getOrganizationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            Organization requester = organizationRepository.findById(apiProperties.getOrganizationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            AccessRequest accessRequest = new AccessRequest();

            accessRequest.setId(UUID.randomUUID());
            accessRequest.setDatasourceId(scope.getDatasourceId());
            accessRequest.setScope(scope.getScope());
            accessRequest.setStatus(AccessRequestStatus.REQUESTED);
            accessRequest.setRequesterAddress(requester.getPublicKeyHash());
            accessRequest.setProviderAddress(provider.getPublicKeyHash());

            accessRequestRepository.save(accessRequest);
        }
    }
}
