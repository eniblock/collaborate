package collaborate.api.datasource.create;


import collaborate.api.datasource.gateway.traefik.model.middleware.Headers;
import collaborate.api.datasource.gateway.traefik.model.middleware.Middleware;
import collaborate.api.datasource.gateway.traefik.model.servertransport.Certificates;
import collaborate.api.datasource.gateway.traefik.model.servertransport.ServersTransport;
import collaborate.api.datasource.gateway.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.gateway.traefik.routing.DatasourceKeySupplier;
import collaborate.api.datasource.model.dto.web.QueryParam;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.http.BasicAuthHeader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class HttpAuthenticationVisitor implements AuthenticationVisitor<Void> {

  private Map<String, Middleware> middlewares;
  private List<QueryParam> queryParams;
  private ServersTransport serversTransport;
  private final AuthHeaderKeySupplier authHeaderKeyFactory;
  private final DatasourceKeySupplier datasourceKeySupplier;
  private final String certificatesPath;

  @Override
  public Void visitBasicAuth(BasicAuth basicAuth) {
    if (middlewares == null) {
      middlewares = new HashMap<>();
    }
    var basicAuthHeader = new BasicAuthHeader(basicAuth.getUser(), basicAuth.getPassword());
    middlewares.put(
        authHeaderKeyFactory.get(),
        Middleware.builder().
            headers(Headers.builder()
                .customRequestHeaders(
                    Map.of("Authorization", basicAuthHeader.getValue())
                ).build())
            .build()
    );

    queryParams = basicAuth.getQueryParams();
    return null;
  }

  @Override
  public Void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    visitBasicAuth(basicAuth);
    String datasourceName = datasourceKeySupplier.get();
    serversTransport = ServersTransport.builder()
        .certificates(List.of(
            Certificates.builder()
                .certFile(Paths.get(certificatesPath, datasourceName + ".crt").toString())
                .keyFile(Paths.get(certificatesPath, datasourceName + ".key").toString())
                .build()
        ))
        .insecureSkipVerify(false)
        .build();
    return null;
  }

  @Override
  public Void visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    if (middlewares == null) {
      middlewares = new HashMap<>();
    }
    serversTransport = ServersTransport.builder()
        .insecureSkipVerify(true)
        .build();
    return null;
  }

}
