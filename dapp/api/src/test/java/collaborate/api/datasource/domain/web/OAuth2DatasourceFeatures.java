package collaborate.api.datasource.domain.web;

import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import collaborate.api.test.TestResources;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

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
              .keywords(
                  new LinkedHashSet<>(List.of("routing-key:documents", "scope:business_data")))
              .url("https://developer.groupe-psa.io/webapi/b2b/api-reference-v3/specification/")
              .build()
          ))).build();

  public static final String datasourceJson  = TestResources.read("/domain/datasource/web/oAuth2Datasource.json");;

  private OAuth2DatasourceFeatures() {
  }

}
