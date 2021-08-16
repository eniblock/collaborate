package collaborate.api.datasource.domain.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.domain.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.authentication.Oauth;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class OAuthDatasourceFeatures {

  public static final WebServerDatasource datasource = WebServerDatasource.builder()
      .id(1L)
      .name("PSA Digital Passport")
      .status(DatasourceStatus.CREATED)
      .keywords(List.of("vehicles", "digital-passport"))
      .baseUrl(URI.create("http://psa.datasource"))
      .authMethod(Oauth.builder()
          .accessMethod("OAUTH2_CLIENT_CREDENTIALS_GRANT")
          .clientId("collaborate")
          .clientSecret("secret")
          .issuerIdentifierUri(URI.create("http://psa.pcc.localhost"))
          .wellKnownURIPathSuffix(URI.create("/.well-known"))
          .transferMethod("OAUTH2_CLIENT_CREDENTIALS_GRANT")
          .build()
      ).resources(
          List.of(WebServerResource.builder()
                  .description("Datasource documents")
                  .keywords(List.of("routing-key:documents", "scope:business_data"))
                  .path("https://developer.groupe-psa.io/webapi/b2b/api-reference-v3/specification/")
                  .build()
          )).build();

  private static final OAuthDatasourceFeatures INSTANCE = new OAuthDatasourceFeatures();
  public final String datasourceJson;

  public OAuthDatasourceFeatures() {
    try {
      datasourceJson = IOUtils.toString(
          Objects.requireNonNull(
              OAuthDatasourceFeatures.class
                  .getResourceAsStream(
                      "/domain/datasource/web/oAuthDatasource.json")
          ), UTF_8.name()
      );
    } catch (IOException e) {
      throw new IllegalStateException("Can't read oAuthDatasource.json");
    }
  }

  public static OAuthDatasourceFeatures getInstance() {
    return INSTANCE;
  }

}
