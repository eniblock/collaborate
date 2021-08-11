package collaborate.api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.ClientCredentialsHttpEntityBody;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.DatasourceClientSecret;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class DatasourceConnectorTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    protected RabbitTemplate rabbitTemplate;

    @Mock
    protected ApiProperties apiProperties;

    @Test
    void getAuthorizationServerMetadata() {
        Datasource datasource = new Datasource();
        datasource.setIssuerIdentifierURI(URI.create("http://foo.bar"));
        datasource.setWellKnownURIPathSuffix("/path");

        DatasourceConnector connector = Mockito.mock(
                DatasourceConnector.class,
                Mockito.withSettings()
                        .useConstructor(restTemplate, rabbitTemplate, apiProperties)
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS)
        );

        AuthorizationServerMetadata authorizationServerMetadataExpected = new AuthorizationServerMetadata();

        when(restTemplate.getForObject(
                datasource.getIssuerIdentifierURI() + datasource.getWellKnownURIPathSuffix(),
                AuthorizationServerMetadata.class
        )).thenReturn(authorizationServerMetadataExpected);

        AuthorizationServerMetadata authorizationServerMetadataActual = connector.getAuthorizationServerMetadata(datasource);

        assertEquals(authorizationServerMetadataExpected, authorizationServerMetadataActual);
    }

    @Test
    public void getAccessToken() {
        DatasourceClientSecret datasourceClientSecret = new DatasourceClientSecret();
        AuthorizationServerMetadata authorizationServerMetadata = new AuthorizationServerMetadata();
        AccessTokenResponse accessTokenExpected = new AccessTokenResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        DatasourceConnector connector = Mockito.mock(
                DatasourceConnector.class,
                Mockito.withSettings()
                        .useConstructor(restTemplate, rabbitTemplate, apiProperties)
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS)
        );

        when(restTemplate.postForObject(
                authorizationServerMetadata.getTokenEndpoint(),
                new HttpEntity<>(new ClientCredentialsHttpEntityBody(datasourceClientSecret), headers),
                AccessTokenResponse.class
        )).thenReturn(accessTokenExpected);

        AccessTokenResponse accessTokenActual = connector.getAccessToken(datasourceClientSecret, authorizationServerMetadata);

        assertEquals(accessTokenExpected, accessTokenActual);
    }
}