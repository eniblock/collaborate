package collaborate.api.gateway;

import collaborate.api.user.security.Authorizations.HasRoles;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
  @GetMapping(value = "**")
  public JsonNode consumeDatasource(HttpServletRequest request) throws IOException {
    return gatewayService.fetch(request);
  }
}
