package collaborate.api.gateway;

import collaborate.api.user.security.Authorizations.HasRoles;
import com.fasterxml.jackson.databind.JsonNode;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(GatewayController.API_V_1_GATEWAY)
@RequiredArgsConstructor
public class GatewayController {

  public static final String API_V_1_GATEWAY = "/api/v1/gateway";
  private final GatewayUrlService gatewayService;

  @PreAuthorize(HasRoles.API_GATEWAY_READ)
  @GetMapping(value = "datasource/{datasourceId}/**")
  public JsonNode consumeDatasource(@PathVariable String datasourceId, HttpServletRequest request) {
    return gatewayService.fetch(datasourceId, request);
  }
}
