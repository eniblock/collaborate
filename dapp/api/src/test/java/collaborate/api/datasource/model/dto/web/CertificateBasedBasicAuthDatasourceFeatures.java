package collaborate.api.datasource.model.dto.web;

import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.test.TestResources;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class CertificateBasedBasicAuthDatasourceFeatures {

  public static final WebServerDatasourceDTO datasource = WebServerDatasourceDTO.builder()
      .id(UUID.fromString("525003f6-f85f-11eb-9a03-0242ac130003"))
      .name("DSPConsortium1 Digital Passport")
      .keywords(new LinkedHashSet<>(List.of("vehicles", "digital-passport")))
      .authMethod(CertificateBasedBasicAuth.builder()
          .user("MWPDRV01")
          .password("BBrlKQ0i")
          .queryParams(new ArrayList<>(List.of(
              new QueryParam("client_id", "c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
          )))
          .passphrase("secret")
          .caEmail("certificate@authority.email")
          .build()
      )
      .baseUrl("https://api-cert-preprod.dspconsortium1.com")
      .resources(
          new ArrayList<>(List.of(
              WebServerResource.builder()
                  .description("Vehicles list")
                  .keywords(new LinkedHashSet<>(List.of("scope:list-asset", "assets")))
                  .url(
                      "/connectedasset/v3/fleets/5fb2830db35c87031c2e0d68/vehicles"
                  ).build(),
              WebServerResource.builder()
                  .description("Last odometer values")
                  .keywords(new LinkedHashSet<>(List.of("scope:metric:odometer", "event_usage")))
                  .url(
                      "/connectedasset/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/status")
                  .queryParams(new ArrayList<>(List.of(
                      QueryParam.builder().key("profile").value("fleet").build(),
                      QueryParam.builder().key("extension").value("odometer").build()
                  )))
                  .build()
          ))
      ).build();
  public static final String datasourceJson = TestResources
      .readPath("/datasource/domain/web/certificateBasedBasicAuthDatasource.json");

  private CertificateBasedBasicAuthDatasourceFeatures() {
  }

  public static WebServerResource getResourceByKeyword(String keyword) {
    return CertificateBasedBasicAuthDatasourceFeatures.datasource
        .getResources().stream()
        .filter(r -> r.getKeywords().stream().anyMatch(k -> k.contains(keyword)))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException(keyword + " keyword not found in webServerResource"));
  }

}
