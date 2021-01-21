package collaborate.api.services;

import collaborate.api.domain.AccessTokenResponse;
import collaborate.api.domain.AuthorizationServerMetadata;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.enumeration.DatasourceEvent;
import collaborate.api.domain.enumeration.GrantType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class DatasourceService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TopicExchange topic;

    @Autowired
    private ObjectMapper objectMapper;

    public void produce(Datasource datasource, DatasourceEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(
                topic.getName(),
                event.getEvent(),
                objectMapper.writeValueAsString(datasource)
        );
    }

    private AuthorizationServerMetadata getAuthorizationServerMetadata(Datasource datasource) {
        return restTemplate.getForObject(
                datasource.getIssuerIdentifierURI() + datasource.getWellKnownURIPathSuffix(),
                AuthorizationServerMetadata.class
        );
    }

    private AccessTokenResponse getAccessToken(Datasource datasource, AuthorizationServerMetadata authorizationServerMetadata) {
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

    public void testConnection(Datasource datasource) {
        AuthorizationServerMetadata authorizationServerMetadata;
        AccessTokenResponse accessTokenResponse;

        try {
            authorizationServerMetadata = this.getAuthorizationServerMetadata(datasource);
            Set<ConstraintViolation<AuthorizationServerMetadata>> violations = validator.validate(authorizationServerMetadata);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        } catch (HttpClientErrorException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Authorization Server Metadata request failed with status %s, check your issuer identifier URI and well-known URI path suffix", exception.getStatusCode()));
        } catch (ConstraintViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization Server Metadata response is invalid, the token endpoint is missing");
        }

        try {
            accessTokenResponse = this.getAccessToken(datasource, authorizationServerMetadata);

            Set<ConstraintViolation<AccessTokenResponse>> violations = validator.validate(accessTokenResponse);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        } catch (HttpClientErrorException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Access token request failed with status %s, check your client id and client secret", exception.getStatusCode()));
        } catch (ConstraintViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access token response is invalid, the access token is missing");
        }

        try {
            HttpHeaders datasourceHeaders = new HttpHeaders();
            datasourceHeaders.setContentType(MediaType.APPLICATION_JSON);
            datasourceHeaders.set("Authorization", "Bearer " + accessTokenResponse.getAccessToken());

            HttpEntity<Void> datasourceEntity = new HttpEntity<Void>(datasourceHeaders);

            ResponseEntity<Void> response = restTemplate.exchange(
                    datasource.getApiURI(),
                    HttpMethod.GET,
                    datasourceEntity,
                    Void.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new HttpClientErrorException(response.getStatusCode());
            }
        } catch (HttpClientErrorException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request of the datasource failed with status %s, check your datasource authorization server", exception.getStatusCode()));
        }
    }
}