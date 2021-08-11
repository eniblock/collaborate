package collaborate.api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import collaborate.api.catalog.CatalogService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.Document;
import collaborate.api.domain.enumeration.DatasourceStatus;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class DatasourceServiceTest {
    @InjectMocks
    DatasourceService datasourceService;

    @Mock
    private RestTemplate restTemplate;


    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TopicExchange topic;

    @Mock
    private DatasourceRepository datasourceRepository;

    @Mock
    private CatalogService catalogService;

    @Mock
    private ApiProperties apiProperties;

    @Mock
    private DatasourceConnectorFactory datasourceConnectorFactory;

    @Test
    void updateStatus() {
        Datasource datasource = new Datasource();
        datasource.setId(1L);
        datasource.setStatus(DatasourceStatus.SYNCHRONIZING);
        datasource.setDocumentCount(1);

        List<Datasource> datasources = Collections.singletonList(datasource);
        Page<Document> page = new PageImpl<Document>(Collections.singletonList(new Document()));

        when(datasourceRepository.findByStatus(DatasourceStatus.SYNCHRONIZING)).thenReturn(datasources);
        when(apiProperties.getOrganizationId()).thenReturn("psa");
        when(catalogService.get(apiProperties.getOrganizationId(), datasource.getId())).thenReturn(page);

        datasourceService.updateStatus();

        assertEquals(DatasourceStatus.SYNCHRONIZED, datasource.getStatus());
    }
}