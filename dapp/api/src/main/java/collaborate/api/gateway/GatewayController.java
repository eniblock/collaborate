package collaborate.api.gateway;

import collaborate.api.user.security.Authorizations.HasRoles;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(GatewayController.API_V_1_GATEWAY)
@RequiredArgsConstructor
@Validated
public class GatewayController {

  public static final String API_V_1_GATEWAY = "/api/v1/gateway";
  private final GatewayUrlService gatewayService;

  @PreAuthorize(HasRoles.API_GATEWAY_READ)
  @GetMapping(value = "datasource/{datasourceId}/{scope}")
  public ResponseEntity<JsonNode> consumeDatasource(
      @PathVariable @NotEmpty String datasourceId,
      @PathVariable @NotEmpty String scope
  ) {
    return consumeDatasource(datasourceId, scope, null);
  }

  @PreAuthorize(HasRoles.API_GATEWAY_READ)
  @GetMapping(value = "datasource/{datasourceId}/{scope}/{assetId}")
  public ResponseEntity<JsonNode> consumeDatasource(
      @PathVariable @NotEmpty String datasourceId,
      @PathVariable @NotEmpty String scope,
      @PathVariable @NotEmpty String assetId
  ) {
    var resourceDTO = GatewayResourceDTO.builder()
        .datasourceId(datasourceId)
        .scope(scope)
        .assetIdForDatasource(assetId)
        .build();
    return gatewayService.fetch(resourceDTO, Optional.empty());
  }
}
