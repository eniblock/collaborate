package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.restclient.ICatalogClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public abstract class DatasourceConnector {

    protected RestTemplate restTemplate;
    protected RabbitTemplate rabbitTemplate;
    protected ICatalogClient catalogClient;
    protected ApiProperties apiProperties;

    public DatasourceConnector(
            RestTemplate restTemplate,
            RabbitTemplate rabbitTemplate,
            ICatalogClient catalogClient,
            ApiProperties apiProperties
    ) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.catalogClient = catalogClient;
        this.apiProperties = apiProperties;
    }

    public abstract Integer synchronize(Datasource datasource, DatasourceClientSecret datasourceClientSecret);

    protected AuthorizationServerMetadata getAuthorizationServerMetadata(Datasource datasource) {
        return restTemplate.getForObject(
                datasource.getIssuerIdentifierURI() + datasource.getWellKnownURIPathSuffix(),
                AuthorizationServerMetadata.class
        );
    }

    protected AccessTokenResponse getAccessToken(DatasourceClientSecret datasourceClientSecret, AuthorizationServerMetadata authorizationServerMetadata) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return restTemplate.postForObject(
                authorizationServerMetadata.getTokenEndpoint(),
                new HttpEntity<>(new ClientCredentialsHttpEntityBody(datasourceClientSecret), headers),
                AccessTokenResponse.class
        );
    }

    protected AccessTokenResponse getAccessToken(DatasourceClientSecret datasourceClientSecret, AuthorizationServerMetadata authorizationServerMetadata, String scope) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return restTemplate.postForObject(
                authorizationServerMetadata.getTokenEndpoint(),
                new HttpEntity<>(new ClientCredentialsHttpEntityBody(datasourceClientSecret, scope), headers),
                AccessTokenResponse.class
        );
    }
}
