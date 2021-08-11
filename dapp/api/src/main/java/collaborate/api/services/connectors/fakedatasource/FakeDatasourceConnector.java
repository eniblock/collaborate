package collaborate.api.services.connectors.fakedatasource;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.BearerHttpHeaders;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.DatasourceClientSecret;
import collaborate.api.domain.Document;
import collaborate.api.services.DatasourceConnector;
import java.util.UUID;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FakeDatasourceConnector extends DatasourceConnector {
    public FakeDatasourceConnector(
            RestTemplate restTemplate,
            RabbitTemplate rabbitTemplate,
            ApiProperties apiProperties
    ) {
        super(restTemplate, rabbitTemplate,  apiProperties);
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
                document.setDocumentUri(metadata.get_links().getDownload().getHref());

                document.setTitle(metadata.getTitle());
                document.setScope(metadata.getScope());
                document.setScopeId(UUID.nameUUIDFromBytes(metadata.getScope().getBytes()));
                document.setType("metadata");
                document.setSynchronizedAt(datasource.getSynchronizedAt());

                // catalogClient.add(document.getOrganizationId(), document.getDatasourceId(), document);
                documentCount++;
                throw new NotImplementedException();
            }
        }

        return documentCount;
    }
}
