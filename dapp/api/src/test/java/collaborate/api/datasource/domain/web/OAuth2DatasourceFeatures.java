package collaborate.api.datasource.domain.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.io.IOUtils;

public class OAuth2DatasourceFeatures {

  public static final WebServerDatasource datasource = WebServerDatasource.builder()
      .id(UUID.fromString("525003f6-f85f-11eb-9a03-0242ac130003"))
      .name("PSA Digital Passport")
      .status(DatasourceStatus.CREATED)
      .keywords(new LinkedHashSet<>(List.of("vehicles", "digital-passport")))
      .baseUrl("http://psa.datasource")
      .authMethod(OAuth2.builder()
          .accessMethod("OAUTH2_CLIENT_CREDENTIALS_GRANT")
          .clientId("collaborate")
          .clientSecret("secret")
          .issuerIdentifierUri(URI.create("http://psa.pcc.localhost"))
          .wellKnownURIPathSuffix(URI.create("/.well-known"))
          .transferMethod("OAUTH2_CLIENT_CREDENTIALS_GRANT")
          .build()
      ).resources(
          new ArrayList<>(List.of(WebServerResource.builder()
                  .description("Datasource documents")
                  .keywords(new LinkedHashSet<>(List.of("routing-key:documents", "scope:business_data")))
                  .url("https://developer.groupe-psa.io/webapi/b2b/api-reference-v3/specification/")
                  .build()
          ))).build();

  private static final OAuth2DatasourceFeatures INSTANCE = new OAuth2DatasourceFeatures();
  public final String datasourceJson;

  public OAuth2DatasourceFeatures() {
    try {
      datasourceJson = IOUtils.toString(
          Objects.requireNonNull(
              OAuth2DatasourceFeatures.class
                  .getResourceAsStream(
                      "/domain/datasource/web/oAuth2Datasource.json")
          ), UTF_8.name()
      );
    } catch (IOException e) {
      throw new IllegalStateException("Can't read oAuth2Datasource.json");
    }
  }

  public static OAuth2DatasourceFeatures getInstance() {
    return INSTANCE;
  }

}
