package collaborate.api.datasource.domain.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import collaborate.api.datasource.domain.web.authentication.CertificateBasedBasicAuth;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.io.IOUtils;

public class CertificateBasedBasicAuthDatasourceFeatures {

  public static final WebServerDatasource datasource = WebServerDatasource.builder()
      .id(UUID.fromString("525003f6-f85f-11eb-9a03-0242ac130003"))
      .name("PSA Digital Passport")
      .status(DatasourceStatus.CREATED)
      .keywords(new LinkedHashSet<>(List.of("vehicles", "digital-passport")))
      .authMethod(CertificateBasedBasicAuth.builder()
          .user("MWPDRV01")
          .password("BBrlKQ0i")
          .queryParams(new ArrayList<>(List.of(
              new QueryParam(1L, "client_id", "c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
          )))
          .passphrase("secret")
          .caEmail("certificate@authority.email")
          .build()
      )
      .baseUrl("https://api-cert-preprod.groupe-psa.com")
      .resources(
          new ArrayList<>(List.of(
              WebServerResource.builder()
                  .description("Vehicles list")
                  .keywords(new LinkedHashSet<>(List.of("routing-key:vehicles", "assets")))
                  .url(
                      "/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles"
                  ).build(),
              WebServerResource.builder()
                  .description("Last odometer values")
                  .keywords(new LinkedHashSet<>(List.of("routing-key:kilometer", "event_usage")))
                  .url(
                      "/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/status")
                  .queryParams(new ArrayList<>(List.of(
                      QueryParam.builder().key("profile").value("fleet").build(),
                      QueryParam.builder().key("extension").value("odometer").build()
                  )))
                  .build(),
              WebServerResource.builder()
                  .description("maintenance")
                  .keywords(new LinkedHashSet<>(List.of("routing-key:maintenance", "event_usage")))
                  .url(
                      "/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/maintenance/$2")
                  .build()
          ))
      ).build();
  private static final CertificateBasedBasicAuthDatasourceFeatures INSTANCE = new CertificateBasedBasicAuthDatasourceFeatures();
  public final String datasourceJson;

  public CertificateBasedBasicAuthDatasourceFeatures() {
    try {
      datasourceJson = IOUtils.toString(
          Objects.requireNonNull(
              CertificateBasedBasicAuthDatasourceFeatures.class
                  .getResourceAsStream(
                      "/domain/datasource/web/certificateBasedBasicAuthDatasource.json")
          ), UTF_8.name()
      );
    } catch (IOException e) {
      throw new IllegalStateException("Can't read certificateBasedBasicAuthDatasource.json");
    }
  }

  public static CertificateBasedBasicAuthDatasourceFeatures getInstance() {
    return INSTANCE;
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
