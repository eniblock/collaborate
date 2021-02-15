package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.Scope;
import collaborate.api.repository.AccessRequestRepository;
import collaborate.api.restclient.ITezosApiGatewayClient;
import collaborate.api.services.AccessRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("access-requests")
public class AccessRequestController {
    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private ITezosApiGatewayClient tezosApiGatewayClient;

    @Autowired
    private AccessRequestService accessRequestService;

    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public void create(@RequestBody Scope[] scopes) {
        accessRequestService.requestAccess(scopes);
    }
}
