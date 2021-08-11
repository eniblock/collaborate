package collaborate.api.services;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.Organization;
import collaborate.api.domain.Scope;
import collaborate.api.repository.AccessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ScopeService {
    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    public AccessRequest getAccessRequest(Scope scope) {
        Organization provider = apiProperties
                .findOrganizationWithOrganizationId(scope.getOrganizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Organization requester = apiProperties.getOrganizations().get(apiProperties.getOrganizationPublicKeyHash());

        AccessRequest accessRequest =
                accessRequestRepository
                        .findFirstByProviderAddressAndRequesterAddressAndDatasourceIdAndScopeIdOrderByCreatedAtDesc(
                                provider.getPublicKeyHash(),
                                requester.getPublicKeyHash(),
                                scope.getDatasourceId(),
                                scope.getScopeId()
                        );

        return accessRequest;
    }
}
