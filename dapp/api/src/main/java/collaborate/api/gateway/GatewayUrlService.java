package collaborate.api.gateway;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.domain.web.WebServerResource;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayUrlService {

  static final Pattern VIN_REGEXP = Pattern
      .compile("(.*/datasource/([A-Za-z0-9-]*)/).*/([A-Za-z0-9-]+)");

  public static final int BASE_URL_GROUP_INDEX = 1;
  public static final int DATASOURCE_UUID_GROUP_INDEX = 2;
  public static final int VIN_GROUP_INDEX = 3;

  private final ApiProperties apiProperties;
  // TODO Use a dataSourceDAO fetching data from SC/IPFS
  private final DatasourceService datasourceService;
  private final GatewayUrlDAO gatewayURLDAO;

  public JsonNode fetch(HttpServletRequest request) throws IOException {
    String apiGatewayTargetURL = replaceBaseUrl(request);

    Matcher matcher = VIN_REGEXP.matcher(apiGatewayTargetURL);
    matcher.find();
    String datasourceUUID = matcher.group(DATASOURCE_UUID_GROUP_INDEX);

    var wsDatasource = datasourceService.getWebServerDatasourceByUUID(datasourceUUID);
    var vinMappingResource = wsDatasource.findVinMappingResource();
    if (vinMappingResource.isPresent()) {
      String vin = matcher.group(VIN_GROUP_INDEX);
      String vehicleId = gatewayURLDAO.getVehicleId(
          datasourceUUID,
          vin,
          vinMappingResource.get().findVinMappingJsonResponsePath(),
          vinMappingResource.flatMap(WebServerResource::findVinMappingQueryParamKey)
      );
      apiGatewayTargetURL = apiGatewayTargetURL.replace(vin, vehicleId);
    }

    return gatewayURLDAO.fetch(apiGatewayTargetURL);
  }

  private String replaceBaseUrl(HttpServletRequest request) {
    return request.getRequestURI().replace(
        GatewayController.API_V_1_GATEWAY,
        apiProperties.getTraefik().getUrl()
    );
  }

}
