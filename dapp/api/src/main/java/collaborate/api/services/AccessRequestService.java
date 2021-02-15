package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.domain.enumeration.AccessRequestStatus;
import collaborate.api.repository.AccessRequestRepository;
import collaborate.api.repository.OrganizationRepository;
import collaborate.api.restclient.ITezosApiGatewayClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AccessRequestService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private ITezosApiGatewayClient tezosApiGatewayClient;

    @Autowired
    private AccessRequestService accessRequestService;

    public void requestAccess(Scope[] scopes) {
        TransactionBatch<AccessRequestParams> transactionBatch = new TransactionBatch<AccessRequestParams>();
        transactionBatch.setSecureKeyName(apiProperties.getOrganizationId());

        List<AccessRequest> accessRequests = new ArrayList<>();

        for (Scope scope : scopes) {
            Organization provider = organizationRepository.findById(scope.getOrganizationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            Organization requester = organizationRepository.findById(apiProperties.getOrganizationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            AccessRequest accessRequest = this.createAccessRequest(scope);

            accessRequest.setRequesterAddress(requester.getPublicKeyHash());
            accessRequest.setProviderAddress(provider.getPublicKeyHash());

            transactionBatch.getTransactions().add(this.createTransaction(accessRequest));
            accessRequests.add(accessRequest);
        }

        try {
            tezosApiGatewayClient.sendTransactionBatch(transactionBatch);
            accessRequestRepository.saveAll(accessRequests);
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    public AccessRequest createAccessRequest(Scope scope) {
        AccessRequest accessRequest = new AccessRequest();

        accessRequest.setId(UUID.randomUUID());
        accessRequest.setDatasourceId(scope.getDatasourceId());
        accessRequest.setScopeId(scope.getScopeId());
        accessRequest.setStatus(AccessRequestStatus.REQUESTED);

        return accessRequest;
    }

    public AccessRequestParams createAccessRequestParams(AccessRequest accessRequest) {
        AccessRequestParams accessRequestParams = new AccessRequestParams();

        accessRequestParams.setId(accessRequest.getId());
        accessRequestParams.setDatasourceId(accessRequest.getDatasourceId());
        accessRequestParams.setScopeId(accessRequest.getScopeId());
        accessRequestParams.setRequesterAddress(accessRequest.getRequesterAddress());
        accessRequestParams.setProviderAddress(accessRequest.getProviderAddress());

        return accessRequestParams;
    }

    public Transaction<AccessRequestParams> createTransaction(AccessRequest accessRequest) {
        Transaction<AccessRequestParams> transaction = new Transaction<AccessRequestParams>();

        transaction.setContractAddress(apiProperties.getContractAddress());
        transaction.setEntryPoint("requestAccess");
        transaction.setEntryPointParams(this.createAccessRequestParams(accessRequest));

        return transaction;
    }
}
