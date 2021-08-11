package collaborate.api.services;

import collaborate.api.catalog.CatalogService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.Document;
import collaborate.api.domain.enumeration.DatasourceStatus;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.services.connectors.DatasourceConnectorFactory;
import java.util.List;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DatasourceService {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    @Autowired
    private DatasourceRepository datasourceRepository;


    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private DatasourceConnectorFactory datasourceConnectorFactory;

    @Scheduled(cron = "*/5 * * * * *")
    public void updateStatus() {
        List<Datasource> datasources = datasourceRepository.findByStatus(DatasourceStatus.SYNCHRONIZING);

        for (Datasource datasource : datasources) {
            Page<Document> data = catalogService.get(apiProperties.getOrganizationId(), datasource.getId());

            if (data.getTotalElements() == datasource.getDocumentCount()) {
                System.out.println("Updating datasource status: " + datasource);
                datasource.setStatus(DatasourceStatus.SYNCHRONIZED);

                datasourceRepository.save(datasource);
            }
        }
    }
}