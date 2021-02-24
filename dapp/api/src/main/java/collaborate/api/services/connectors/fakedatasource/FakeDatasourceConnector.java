package collaborate.api.services.connectors.fakedatasource;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.DatasourceConnector;
import collaborate.api.services.TransactionsEventConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class FakeDatasourceConnector extends DatasourceConnector {
    Logger logger = LoggerFactory.getLogger(FakeDatasourceConnector.class);

    public FakeDatasourceConnector(
            RestTemplate restTemplate,
            RabbitTemplate rabbitTemplate,
            ICatalogClient catalogClient,
            ApiProperties apiProperties
    ) {
        super(restTemplate, rabbitTemplate, catalogClient, apiProperties);
    }

    public Integer synchronize(Datasource datasource, DatasourceClientSecret datasourceClientSecret) {
        AuthorizationServerMetadata authorizationServerMetadata = this.getAuthorizationServerMetadata(datasource);
        AccessTokenResponse accessTokenResponse = this.getAccessToken(datasourceClientSecret, authorizationServerMetadata);

        Traverson traverson = new Traverson(datasource.getApiURI(), MediaTypes.HAL_JSON);


        Traverson.TraversalBuilder builder = traverson
                .follow()
                .withHeaders(new BearerHttpHeaders(accessTokenResponse));

        ParameterizedTypeReference<CollectionModel<Metadata>> resourceParameterizedTypeReference = new ParameterizedTypeReference<CollectionModel<Metadata>>() {
        };
        CollectionModel<Metadata> collection = builder.toObject(resourceParameterizedTypeReference);

        Integer documentCount = 0;

        if (null != collection) {
            for (Metadata metadata : collection) {
                Document document = new Document();

                document.setOrganizationId(this.apiProperties.getOrganizationId());
                document.setOrganizationName(this.apiProperties.getOrganizationName());
                document.setDatasourceId(datasource.getId());
                document.setDocumentId(metadata.getId());
                try {
                    document.setDocumentUri(new URI(metadata.get_links().getDownload().getHref()));
                } catch (URISyntaxException e) {
                    logger.error("Could not create download uri: " + e.getMessage());
                }
                document.setTitle(metadata.getTitle());
                document.setScope(metadata.getScope());
                document.setScopeId(UUID.nameUUIDFromBytes(metadata.getScope().getBytes()));
                document.setType("metadata");
                document.setSynchronizedAt(datasource.getSynchronizedAt());

                System.out.println(document);

                catalogClient.add(document.getOrganizationId(), document.getDatasourceId(), document);
                documentCount++;
            }
        }

        return documentCount;
    }
}
