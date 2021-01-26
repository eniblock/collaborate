package collaborate.catalog.controller;

import collaborate.catalog.domain.Data;
import collaborate.catalog.repository.DataRepository;
import org.keycloak.KeycloakPrincipal;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("principal.getKeycloakSecurityContext().getToken().getIssuedFor() == #organizationName")
    public ResponseEntity<Data> add(@AuthenticationPrincipal KeycloakPrincipal principal, @PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId, @RequestBody Data data) {
        // TODO data validation (organization, datasource, etc)

        rabbitTemplate.convertAndSend(
                topic.getName(),
                "data.create",
                data
        );

        return ResponseEntity.ok(data);
    }

    @DeleteMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    @PreAuthorize("principal.getKeycloakSecurityContext().getToken().getIssuedFor() == #organizationName")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal KeycloakPrincipal principal, @PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId) {
        dataRepository.deleteByOrganizationNameAndDatasourceId(organizationName, datasourceId);
    }

    @GetMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    @PreAuthorize("principal.getKeycloakSecurityContext().getToken().getIssuedFor() == #organizationName")
    public Page<Data> list(@AuthenticationPrincipal KeycloakPrincipal principal, @PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId, Pageable pageable) {
        Page<Data> dataPage = dataRepository.findByOrganizationNameAndDatasourceId(organizationName, datasourceId, pageable);

        return dataPage;
    }
}
