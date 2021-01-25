package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.enumeration.GrantType;
import collaborate.api.restclient.ICatalogClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    public abstract Integer synchronize(Datasource datasource);

    protected AuthorizationServerMetadata getAuthorizationServerMetadata(Datasource datasource) {
        return restTemplate.getForObject(
                datasource.getIssuerIdentifierURI() + datasource.getWellKnownURIPathSuffix(),
                AuthorizationServerMetadata.class
        );
    }

    protected AccessTokenResponse getAccessToken(Datasource datasource, AuthorizationServerMetadata authorizationServerMetadata) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", GrantType.client_credentials.toString());
        map.add("client_id", datasource.getClientId());
        map.add("client_secret", datasource.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(
                authorizationServerMetadata.getTokenEndpoint(),
                entity,
                AccessTokenResponse.class
        );
    }
}
