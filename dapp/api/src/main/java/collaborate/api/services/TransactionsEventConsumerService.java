package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.restclient.ITezosApiGatewayClient;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;

import java.util.Optional;

@Service
public class TransactionsEventConsumerService {
    Logger logger = LoggerFactory.getLogger(TransactionsEventConsumerService.class);

    @Autowired
    private ITezosApiGatewayClient tezosApiGatewayClient;

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
                    @Argument(name = "contractAddress", value = "KT1TTCPr7Mocjuw6qBZFd3pkJoDU1XPZTVNy"),
                    @Argument(name = "x-match", value = "all")
            },
            key = ""))
    void listen(RequestAccessMessage message) {
        logger.info("Received message: " + message.toString());

        Organization myOrganization = apiProperties.getOrganizations().get(apiProperties.getOrganizationId());

        String providerAddress = message.getParameters().getValue().getRequestAccess().getProviderAddress();

        Long dataSourceId = message.getParameters().getValue().getRequestAccess().getDatasourceId();

        Optional<Datasource> optionalDatasource = datasourceRepository.findById(dataSourceId);

        if (!optionalDatasource.isPresent()) {
            return;
        }

        Datasource datasource = optionalDatasource.get();

        logger.info("Found data source: " + datasource.getId());
        logger.info("Myorganization: " + myOrganization.getPublicKeyHash());
        logger.info("providerAddress: " + providerAddress);
        logger.info("COMPARE: " + !providerAddress.equalsIgnoreCase(myOrganization.getPublicKeyHash()));

        if (!providerAddress.equalsIgnoreCase(myOrganization.getPublicKeyHash())) {
            return;
        }

        DatasourceConnector connector = datasourceConnectorFactory.create(datasource);

        DatasourceClientSecret datasourceClientSecret = vaultKeyValueOperations.get("datasources/" + datasource.getId(), DatasourceClientSecret.class).getData();

        AuthorizationServerMetadata authorizationServerMetadata = connector.getAuthorizationServerMetadata(datasource);

        AccessTokenResponse token = connector.getAccessToken(datasourceClientSecret, authorizationServerMetadata);

        System.out.println("TOKEN: " + token.getAccessToken());

        AccessGrantParams params = new AccessGrantParams();

        params.setId(message.getParameters().getValue().getRequestAccess().getId());
        params.setJwtToken(token.getAccessToken());

        System.out.println("Access GRANT params: " + params.toString());

        accessGrantService.addAccessGrant(params);
    }
}