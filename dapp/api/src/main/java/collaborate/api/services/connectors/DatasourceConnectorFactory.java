package collaborate.api.services.connectors;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.Datasource;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.DatasourceConnector;
import collaborate.api.services.connectors.fakedatasource.FakeDatasourceConnector;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DatasourceConnectorFactory {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private ApiProperties apiProperties;

    public DatasourceConnector create(Datasource datasource) {
        return new FakeDatasourceConnector(restTemplate, rabbitTemplate, catalogClient, apiProperties);
    }
}
