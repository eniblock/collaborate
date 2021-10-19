package collaborate.api.gateway;

import collaborate.api.config.api.TraefikProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayUrlService {

  private final TraefikProperties traefikProperties;
  // TODO Use a dataSourceDAO fetching data from SC/IPFS
  private final GatewayUrlDAO gatewayURLDAO;

  public JsonNode fetch(HttpServletRequest request) throws IOException {
    String apiGatewayTargetURL = replaceBaseUrl(request);

    return gatewayURLDAO.fetch(apiGatewayTargetURL);
  }

  String replaceBaseUrl(HttpServletRequest request) {
    return UriComponentsBuilder.fromUriString(request.getRequestURI().replace(
        GatewayController.API_V_1_GATEWAY,
        traefikProperties.getUrl())).build().toUriString();
  }

}
