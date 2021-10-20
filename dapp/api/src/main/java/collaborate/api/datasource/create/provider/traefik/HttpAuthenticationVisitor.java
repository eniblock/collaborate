package collaborate.api.datasource.create.provider.traefik;


import collaborate.api.datasource.model.dto.web.QueryParam;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.datasource.model.traefik.middleware.Headers;
import collaborate.api.datasource.model.traefik.middleware.Middleware;
import collaborate.api.datasource.model.traefik.servertransport.Certificates;
import collaborate.api.datasource.model.traefik.servertransport.ServersTransport;
import collaborate.api.datasource.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.traefik.routing.DatasourceKeySupplier;
import collaborate.api.http.BasicAuthHeader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;

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
                    Map.of(BasicAuthHeader.KEY, basicAuthHeader.getValue())
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
  public Void visitOAuth2(OAuth2 oAuth2) {
    // TODO v0.4.0
    throw new NotImplementedException("oAuth2");
  }

}
