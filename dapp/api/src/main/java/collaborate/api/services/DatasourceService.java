package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.domain.enumeration.DatasourceEvent;
import collaborate.api.domain.enumeration.DatasourceStatus;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private DatasourceConnectorFactory datasourceConnectorFactory;

    @Autowired
    private DatasourceHttpEntityFactory datasourceHttpEntityFactory;

    public void produce(Datasource datasource, DatasourceEvent event) {
        rabbitTemplate.convertAndSend(
                topic.getName(),
                event.getEvent(),
                datasource
        );
    }

    @RabbitListener(queues = "#{datasourceSynchronizeQueue.name}")
    public void synchronize(Datasource datasource) {
        Optional<Datasource> optionalDatasource = datasourceRepository.findById(datasource.getId());

        if (optionalDatasource.isPresent()) {
            datasource = optionalDatasource.get();

            if (datasource.getStatus() != DatasourceStatus.SYNCHRONIZING) {
                System.out.println("Synchronizing " + datasource.getName());
                datasource.setStatus(DatasourceStatus.SYNCHRONIZING);
                datasourceRepository.save(datasource);

                catalogClient.delete(this.apiProperties.getOrganizationName(), datasource.getId());

                DatasourceConnector connector = datasourceConnectorFactory.create(datasource);
                Integer dataCount = connector.synchronize(datasource);

                datasource.setDataCount(dataCount);

                datasourceRepository.save(datasource);
            }
        }
    }

    public void testConnection(Datasource datasource) {
        DatasourceConnector connector = datasourceConnectorFactory.create(datasource);

        AuthorizationServerMetadata authorizationServerMetadata;
        AccessTokenResponse accessTokenResponse;

        try {
            authorizationServerMetadata = connector.getAuthorizationServerMetadata(datasource);
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
            accessTokenResponse = connector.getAccessToken(datasource, authorizationServerMetadata);

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
            ResponseEntity<Void> response = restTemplate.exchange(
                    datasource.getApiURI(),
                    HttpMethod.GET,
                    datasourceHttpEntityFactory.create(accessTokenResponse),
                    Void.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new HttpClientErrorException(response.getStatusCode());
            }
        } catch (HttpClientErrorException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request of the datasource failed with status %s, check your datasource authorization server", exception.getStatusCode()));
        }
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void updateStatus() {
        List<Datasource> datasources = datasourceRepository.findByStatus(DatasourceStatus.SYNCHRONIZING);

        for (Datasource datasource : datasources) {
            Page<Data> data = catalogClient.get(apiProperties.getOrganizationName(), datasource.getId());

            if (data.getTotalElements() == datasource.getDataCount()) {
                System.out.println("Updating datasource status: " + datasource);
                datasource.setStatus(DatasourceStatus.SYNCHRONIZED);

                datasourceRepository.save(datasource);
            }
        }
    }
}