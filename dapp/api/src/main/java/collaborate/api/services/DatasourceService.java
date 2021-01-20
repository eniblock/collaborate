package collaborate.api.services;

import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.enumeration.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DatasourceService {
    @Autowired
    private RestTemplate restTemplate;

    public void testConnection(Datasource datasource) {
        try {
            AuthorizationServerMetadata authorizationServerMetadata = restTemplate.getForObject(
                    datasource.getIssuerIdentifierURI() + datasource.getWellKnownURIPathSuffix(),
                    AuthorizationServerMetadata.class
            );

            authorizationServerMetadata.getTokenEndpoint();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", GrantType.client_credentials.toString());
            map.add("client_id", datasource.getClientId());
            map.add("client_secret", datasource.getClientSecret());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            try {
                AccessTokenResponse accessTokenResponse = restTemplate.postForObject(
                        authorizationServerMetadata.getTokenEndpoint(),
                        entity,
                        AccessTokenResponse.class
                );
            } catch (HttpClientErrorException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access token request of the datasource failed: " + exception.getMessage());
            }
        } catch (HttpClientErrorException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization server metadata request of the datasource failed: " + exception.getMessage());
        }
    }
}