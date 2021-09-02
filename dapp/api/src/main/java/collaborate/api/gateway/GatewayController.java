package collaborate.api.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/gateway")
@RequiredArgsConstructor
public class GatewayController {

  private final ObjectMapper objectMapper;

  @GetMapping(value="**")
  public JsonNode consumeDatasource(HttpServletRequest request)
      throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    var apiGatewayTargetURL = request.getRequestURI().replace("/api/v1/gateway", "https://localhost:8443");
    log.debug("Calling api-gateway with url={}", apiGatewayTargetURL);
    var client = HttpClients
        .custom()
        .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        .build();
    var response = client.execute(new HttpGet(apiGatewayTargetURL));
    return objectMapper.readTree(EntityUtils.toString(response.getEntity()));
  }
}
