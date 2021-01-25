package collaborate.catalog.controller;

import collaborate.catalog.domain.Data;
import collaborate.catalog.repository.DataRepository;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TopicExchange topic;

    @Autowired
    private DataRepository dataRepository;

    @PostMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    public ResponseEntity<Data> add(@PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId, @RequestBody Data data) {
        // TODO data validation (organization, datasource, etc)

        rabbitTemplate.convertAndSend(
                topic.getName(),
                "data.create",
                data
        );

        return ResponseEntity.ok(data);
    }

    @DeleteMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId) {
        dataRepository.deleteByOrganizationNameAndDatasourceId(organizationName, datasourceId);
    }

    @GetMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    public Page<Data> list(@PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId, Pageable pageable) {
        Page<Data> dataPage = dataRepository.findByOrganizationNameAndDatasourceId(organizationName, datasourceId, pageable);

        return dataPage;
    }
}
