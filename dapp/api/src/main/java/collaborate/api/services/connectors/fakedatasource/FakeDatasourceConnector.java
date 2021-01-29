package collaborate.api.services.connectors.fakedatasource;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.DatasourceConnector;
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

        Integer dataCount = 0;

        if (null != collection) {
            for (Metadata metadata : collection) {
                Data data = new Data();

                data.setOrganizationName(this.apiProperties.getOrganizationName());
                data.setDatasourceId(datasource.getId());
                data.setDataId(metadata.getId());
                data.setTitle(metadata.getTitle());
                data.setScope(metadata.getScope());
                data.setType("metadata");

                System.out.println(data);

                catalogClient.add(data.getOrganizationName(), data.getDatasourceId(), data);
                dataCount++;
            }
        }

        return dataCount;
    }
}
