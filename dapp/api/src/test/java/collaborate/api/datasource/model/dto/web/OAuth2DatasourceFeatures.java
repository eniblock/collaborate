package collaborate.api.datasource.model.dto.web;

import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.test.TestResources;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class OAuth2DatasourceFeatures {

  public static final WebServerDatasourceDTO datasource = WebServerDatasourceDTO.builder()
      .id(UUID.fromString("525003f6-f85f-11eb-9a03-0242ac130003"))
      .name("DSPConsortium1 Digital Passport")
      .keywords(new LinkedHashSet<>(List.of("vehicles", "digital-passport")))
      .baseUrl("http://DSPConsortium1.datasource")
      .authMethod(OAuth2ClientCredentialsGrant.builder()
          .grantType("OAUTH2_CLIENT_CREDENTIALS_GRANT")
          .clientId("collaborate")
          .clientSecret("secret")
          .tokenEndpoint(URI.create("http://dspconsortium1.localhost/token"))
          .build()
      ).resources(
          new ArrayList<>(List.of(WebServerResource.builder()
              .description("Datasource documents")
              .keywords(
                  new LinkedHashSet<>(
                      List.of(
                          Attribute.builder()
                              .name("routing-key")
                              .value("documents").build(),
                          Attribute.builder()
                              .name("scope")
                              .value("business_data").build(),
                          Attribute.builder()
                              .name("list-asset").build()
                      )
                  )
              ).url(
                  "https://developer.dspconsortium1.io/webapi/b2b/api-reference-v3/specification/")
              .build()
          ))).build();

  public static final String datasourceJson = TestResources.readContent(
      "/datasource/model/web/oAuth2Datasource.json");

  private OAuth2DatasourceFeatures() {
  }

}
