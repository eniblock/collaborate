package collaborate.api.datasource.traefik.mapping;


import collaborate.api.datasource.domain.web.QueryParam;
import collaborate.api.datasource.domain.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.domain.web.authentication.BasicAuth;
import collaborate.api.datasource.domain.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import collaborate.api.datasource.traefik.routing.DatasourceNameSupplier;
import collaborate.api.http.BasicAuthHeader;
import collaborate.api.traefik.domain.middleware.Headers;
import collaborate.api.traefik.domain.middleware.Middleware;
import collaborate.api.traefik.domain.servertransport.Certificates;
import collaborate.api.traefik.domain.servertransport.ServersTransport;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;

@Data
public class HttpAuthenticationVisitor implements AuthenticationVisitor {

  private Map<String, Middleware> middlewares;
  private List<QueryParam> queryParams;
  private ServersTransport serversTransport;
  private final DatasourceNameSupplier datasourceNameSupplier;
  private final String certificatesPath;

  @Override
  public void visitBasicAuth(BasicAuth basicAuth) {
    if (middlewares == null) {
      middlewares = new HashMap<>();
    }
    var basicAuthHeader = new BasicAuthHeader(basicAuth.getUser(), basicAuth.getPassword());
    middlewares.put(
        datasourceNameSupplier.get() + "-auth-headers",
        Middleware.builder().
            headers(Headers.builder()
                .customRequestHeaders(
                    Map.of(BasicAuthHeader.KEY, basicAuthHeader.getValue())
                ).build())
            .build()
    );

    queryParams = basicAuth.getQueryParams();
  }

  @Override
  public void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    visitBasicAuth(basicAuth);
    String datasourceName = datasourceNameSupplier.get();
    serversTransport = ServersTransport.builder()
        .certificates(List.of(
            Certificates.builder()
                .certFile(Paths.get(certificatesPath, datasourceName + ".crt").toString())
                .keyFile(Paths.get(certificatesPath, datasourceName + ".key").toString())
                .build()
        ))
        .insecureSkipVerify(false)
        .build();
  }

  @Override
  public void visitOAuth2(OAuth2 oAuth2) {
    // TODO v0.4.0
    throw new NotImplementedException("oAuth2");
  }

}
