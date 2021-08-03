package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.AccessGrantParams;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.DatasourceClientSecret;
import collaborate.api.domain.GrantAccessValue;
import collaborate.api.domain.RequestAccessValue;
import collaborate.api.domain.Scope;
import collaborate.api.domain.TransactionsEventMessage;
import collaborate.api.domain.enumeration.AccessRequestStatus;
import collaborate.api.repository.AccessRequestRepository;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private AccessGrantService accessGrantService;

    @Autowired
    private CipherService cipherService;

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private VaultService vaultService;

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

        RequestAccessValue value = (RequestAccessValue) message.getParameters().getValue();
        String providerAddress = value.getRequestAccess().getProviderAddress();

        if (!providerAddress.equalsIgnoreCase(apiProperties.getOrganizationPublicKeyHash())) {
            return;
        }

        // Find the datasource by id
        Long dataSourceId = value.getRequestAccess().getDatasourceId();

        Datasource datasource = datasourceRepository.findById(dataSourceId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        logger.info("Found data source: " + datasource.getName());

        // Find scope by id
        UUID scopeId = value.getRequestAccess().getScopeId();
        Scope scope = catalogClient.getScopeById(scopeId);

        logger.info("Found scope: " + scope.getScope());

        DatasourceConnector connector = datasourceConnectorFactory.create(datasource);

        DatasourceClientSecret datasourceClientSecret = vaultService.get("datasources/" + datasource.getId(), DatasourceClientSecret.class);

        AuthorizationServerMetadata authorizationServerMetadata = connector.getAuthorizationServerMetadata(datasource);

        AccessTokenResponse token = connector.getAccessToken(datasourceClientSecret, authorizationServerMetadata, scope.getScope());

        try {
            // Begin to ciphered the token
            String requesterAddress = value.getRequestAccess().getRequesterAddress();

            String publicKeyAsString = apiProperties.getOrganizations().get(requesterAddress).getPublicKey();
            PublicKey publicKey = CipherService.getKey(publicKeyAsString);

            String cipheredToken = cipherService.cipher(token.getAccessToken(), publicKey);

            // Prepare the parameters for the grant access transaction
            AccessGrantParams params = new AccessGrantParams();

            params.setId(value.getRequestAccess().getId());
            params.setJwtToken(cipheredToken);
            params.setProviderAddress(value.getRequestAccess().getProviderAddress());
            params.setRequesterAddress(value.getRequestAccess().getRequesterAddress());

            logger.info("Add the grant access for this request: " + params.getId());

            accessGrantService.addAccessGrant(params);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
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

        GrantAccessValue value = (GrantAccessValue) message.getParameters().getValue();

        if (!value.getGrantAccess().getRequesterAddress().equalsIgnoreCase(apiProperties.getOrganizationPublicKeyHash())) {
            return;
        }

        // This part is to test the token is correctly encrypted
        // Should be move to the download process in the future
        String privateKeyAsString = apiProperties.getOrganizationPrivateKey();
        PrivateKey privateKey = CipherService.getPrivateKey(privateKeyAsString);

        try {
            System.out.println("DECIPHERED TOKEN: " + cipherService.decipher(value.getGrantAccess().getJwtToken(), privateKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        UUID requestID = value.getGrantAccess().getId();

        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(requestID);

        if(!optionalAccessRequest.isPresent()) {
            return;
        }

        AccessRequest accessRequest = optionalAccessRequest.get();

        accessRequest.setStatus(AccessRequestStatus.GRANTED);
        accessRequest.setJwtToken(value.getGrantAccess().getJwtToken());

        accessRequestRepository.save(accessRequest);
    }
}