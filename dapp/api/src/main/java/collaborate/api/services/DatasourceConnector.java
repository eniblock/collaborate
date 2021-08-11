package collaborate.api.services;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.ClientCredentialsHttpEntityBody;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.DatasourceClientSecret;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Deprecated
public abstract class DatasourceConnector {

    protected RestTemplate restTemplate;
    protected RabbitTemplate rabbitTemplate;
    protected ApiProperties apiProperties;

    public DatasourceConnector(
            RestTemplate restTemplate,
            RabbitTemplate rabbitTemplate,
            ApiProperties apiProperties
    ) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
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
