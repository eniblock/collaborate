package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.domain.enumeration.DatasourceEvent;
import collaborate.api.domain.enumeration.DatasourceStatus;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.validation.Validator;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class DatasourceServiceTest {
    @InjectMocks
    DatasourceService datasourceService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Validator validator;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TopicExchange topic;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DatasourceRepository datasourceRepository;

    @Mock
    private ICatalogClient catalogClient;

    @Mock
    private ApiProperties apiProperties;

    @Mock
    private DatasourceConnectorFactory datasourceConnectorFactory;

    @Test()
    void produce() {
        Datasource datasource = new Datasource();
        when(topic.getName()).thenReturn("datasource");

        datasourceService.produce(datasource, DatasourceEvent.CREATED);

        verify(rabbitTemplate, times(1)).convertAndSend(
                "datasource",
                DatasourceEvent.CREATED.getEvent(),
                datasource
        );
    }

    @Test
    void synchronize() {
        Datasource datasource = new Datasource();
        datasource.setStatus(DatasourceStatus.CREATED);
        Optional<Datasource> optionalDatasource = Optional.of(datasource);
        ResponseEntity<Void> deleteEntity = ResponseEntity.noContent().build();
        DatasourceConnector connector = mock(DatasourceConnector.class);

        when(datasourceRepository.findById(datasource.getId())).thenReturn(optionalDatasource);
        when(apiProperties.getOrganizationName()).thenReturn("psa");
        when(catalogClient.delete(apiProperties.getOrganizationName(), datasource.getId())).thenReturn(deleteEntity);
        when(datasourceConnectorFactory.create(datasource)).thenReturn(connector);
        when(connector.synchronize(datasource)).thenReturn(1);

        datasourceService.synchronize(datasource);

        assertEquals(DatasourceStatus.SYNCHRONIZING, datasource.getStatus());
        assertEquals(1, datasource.getDataCount());
    }

    @Test
    void testConnection() {
        Datasource datasource = new Datasource();
        datasource.setApiURI(URI.create("http://foo.bar"));

        DatasourceConnector connector = mock(DatasourceConnector.class);
        AuthorizationServerMetadata authorizationServerMetadata = mock(AuthorizationServerMetadata.class);
        AccessTokenResponse accessTokenResponse = mock(AccessTokenResponse.class);

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        HttpEntity<Void> datasourceHttpEntity = new HttpEntity<Void>(new BearerHttpHeaders(accessTokenResponse));

        when(datasourceConnectorFactory.create(datasource)).thenReturn(connector);
        when(connector.getAuthorizationServerMetadata(datasource)).thenReturn(authorizationServerMetadata);
        when(connector.getAccessToken(datasource, authorizationServerMetadata)).thenReturn(accessTokenResponse);
        when(connector.getAccessToken(datasource, authorizationServerMetadata)).thenReturn(accessTokenResponse);

        when(restTemplate.exchange(
                datasource.getApiURI(),
                HttpMethod.GET,
                datasourceHttpEntity,
                Void.class
        )).thenReturn(response);

        assertDoesNotThrow(() -> datasourceService.testConnection(datasource));
    }

    @Test
    void updateStatus() {
        Datasource datasource = new Datasource();
        datasource.setId(1L);
        datasource.setStatus(DatasourceStatus.SYNCHRONIZING);
        datasource.setDataCount(1);

        List<Datasource> datasources = Collections.singletonList(datasource);
        Page<Data> page = new PageImpl<Data>(Collections.singletonList(new Data()));

        when(datasourceRepository.findByStatus(DatasourceStatus.SYNCHRONIZING)).thenReturn(datasources);
        when(apiProperties.getOrganizationName()).thenReturn("psa");
        when(catalogClient.get(apiProperties.getOrganizationName(), datasource.getId())).thenReturn(page);

        datasourceService.updateStatus();

        assertEquals(DatasourceStatus.SYNCHRONIZED, datasource.getStatus());
    }
}