package collaborate.api.datasource.domain.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.domain.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class CertificateBasedBasicAuthDatasourceFeatures {

  public static final WebServerDatasource datasource = WebServerDatasource.builder()
      .id(1L)
      .name("PSA Digital Passport")
      .status(DatasourceStatus.CREATED)
      .keywords(List.of("vehicles", "digital-passport"))
      .baseUrl(URI.create("baseUrl"))
      .authMethod(CertificateBasedBasicAuth.builder()
          .user("dsUserSample")
          .password("dsPwdSample")
          .queryParams(List.of(
              new QueryParam(1L,"client_id", "c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
          ))
          .host("api-cert-preprod.groupe-psa.com")
          .passphrase("secret")
          .caEmail("certificate@authority.email")
          .build()
      ).resources(
          List.of(WebServerResource.builder()
                  .description("Datasource documentation")
                  .keywords(List.of("documentation", "api_documentation"))
                  .path("https://developer.groupe-psa.io/webapi/b2b/api-reference-v3/specification/")
                  .build(),
              WebServerResource.builder()
                  .description("Vehicles list")
                  .keywords(List.of("vehicles", "assets"))
                  .path(
                      "https://api-cert-preprod.groupe-psa.com/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles"
                  ).build(),
              WebServerResource.builder()
                  .description("Last odometer values")
                  .keywords(List.of("metric", "metric_kilometer", "event_usage"))
                  .path(
                      "https://api-cert-preprod.groupe-psa.com/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/{{vid1}}/status?profile=fleet&extension=odometer"
                  ).pathParams(List.of(
                      new PathParam(1L,"{{vid1}}", "vid")
                  )).build(),
              WebServerResource.builder()
                  .description("maintenance")
                  .keywords(List.of("maintenance", "event_usage"))
                  .path(
                      "https://api-cert-preprod.groupe-psa.com/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/{{vid1}}/maintenance"
                  ).pathParams(List.of(
                      new PathParam(1L,"{{vid1}}", "vid")
                  )).build()
          )).build();
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

}
