package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.domain.enumeration.AccessRequestStatus;
import collaborate.api.repository.AccessRequestRepository;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionsEventConsumerService {
    Logger logger = LoggerFactory.getLogger(TransactionsEventConsumerService.class);

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private DatasourceConnectorFactory datasourceConnectorFactory;

    @Autowired
    private VaultKeyValueOperations vaultKeyValueOperations;

    @Autowired
    private AccessGrantService accessGrantService;

    @RabbitListener(containerFactory = "tezos-api-gateway", bindings = @QueueBinding(
            value = @Queue(
                    name = "",
                    arguments = {
                            @Argument(name = "exclusive", value = "true")
                    }
            ),
            exchange = @Exchange(name = "headers-exchange", type = "headers"),
            arguments = {
                    @Argument(name = "entrypoint", value = "requestAccess"),
                    @Argument(name = "contractAddress", value = "#{contractAddress}"),
                    @Argument(name = "x-match", value = "all")
            },
            key = ""))
    void listenRequestAccess(TransactionsEventMessage<RequestAccessValue> message) {
        logger.info("Received message: " + message.toString());

        Organization myOrganization = apiProperties.getOrganizations().get(apiProperties.getOrganizationId());

        RequestAccessValue value = (RequestAccessValue) message.getParameters().getValue();
        String providerAddress = value.getRequestAccess().getProviderAddress();

        Long dataSourceId = value.getRequestAccess().getDatasourceId();

        Optional<Datasource> optionalDatasource = datasourceRepository.findById(dataSourceId);

        if (!optionalDatasource.isPresent()) {
            return;
        }

        Datasource datasource = optionalDatasource.get();

        logger.info("Found data source: " + datasource.getId());

        if (!providerAddress.equalsIgnoreCase(myOrganization.getPublicKeyHash())) {
            return;
        }

        DatasourceConnector connector = datasourceConnectorFactory.create(datasource);

        DatasourceClientSecret datasourceClientSecret = vaultKeyValueOperations.get("datasources/" + datasource.getId(), DatasourceClientSecret.class).getData();

        AuthorizationServerMetadata authorizationServerMetadata = connector.getAuthorizationServerMetadata(datasource);

        AccessTokenResponse token = connector.getAccessToken(datasourceClientSecret, authorizationServerMetadata);

        AccessGrantParams params = new AccessGrantParams();

        params.setId(value.getRequestAccess().getId());
        params.setJwtToken(token.getAccessToken());
        params.setProviderAddress(value.getRequestAccess().getProviderAddress());
        params.setRequesterAddress(value.getRequestAccess().getRequesterAddress());

        logger.info("Add the grant access for this request: " + params.getId());

        accessGrantService.addAccessGrant(params);
    }

    @RabbitListener(containerFactory = "tezos-api-gateway", bindings = @QueueBinding(
            value = @Queue(
                    name = "",
                    arguments = {
                            @Argument(name = "exclusive", value = "true")
                    }
            ),
            exchange = @Exchange(name = "headers-exchange", type = "headers"),
            arguments = {
                    @Argument(name = "entrypoint", value = "grantAccess"),
                    @Argument(name = "contractAddress", value = "#{contractAddress}"),
                    @Argument(name = "x-match", value = "all")
            },
            key = ""))
    void listenGrantAccess(TransactionsEventMessage<GrantAccessValue> message) {
        logger.info("Received message: " + message.toString());

        Organization myOrganization = apiProperties.getOrganizations().get(apiProperties.getOrganizationId());

        GrantAccessValue value = (GrantAccessValue) message.getParameters().getValue();

        if (!value.getGrantAccess().getRequesterAddress().equalsIgnoreCase(myOrganization.getPublicKeyHash())) {
            return;
        }

        UUID requestID = value.getGrantAccess().getId();

        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(requestID);

        if(!optionalAccessRequest.isPresent()) {
            return;
        }

        AccessRequest accessRequest = optionalAccessRequest.get();

        accessRequest.setStatus(AccessRequestStatus.GRANTED);

        accessRequestRepository.save(accessRequest);
    }
}