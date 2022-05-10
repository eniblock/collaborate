package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;

import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.transfer.CertificateBasedAuthorityEmail;
import collaborate.api.test.TestResources;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class CertificateBasedBasicAuthDatasourceFeatures {

  public static final WebServerDatasourceDTO datasource = WebServerDatasourceDTO.builder()
      .id(UUID.fromString("525003f6-f85f-11eb-9a03-0242ac130003"))
      .name("DSPConsortium1 Digital Passport")
      .type("WebServerDatasource")
      .keywords(new LinkedHashSet<>(List.of("vehicles", "digital-passport")))
      .authMethod(CertificateBasedBasicAuth.builder()
          .user("MWPDRV01")
          .password("BBrlKQ0i")
          .queryParams(new ArrayList<>(List.of(
              new QueryParam("client_id", "c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
          )))
          .passphrase("secret")
          .partnerTransferMethod(new CertificateBasedAuthorityEmail("certificate@authority.email"))
          .build()
      )
      .baseUrl("https://api-cert-preprod.dspconsortium1.com")
      .resources(
          new ArrayList<>(List.of(
              WebServerResource.builder()
                  .description("Vehicles list")
                  .keywords(new LinkedHashSet<>(
                          List.of(
                              Attribute.builder()
                                  .name("scope")
                                  .value("referentials").build(),
                              Attribute.builder()
                                  .name("list-asset").build()
                          )
                      )
                  ).url(
                      "/connectedasset/v3/fleets/5fb2830db35c87031c2e0d68/vehicles"
                  ).build(),
              WebServerResource.builder()
                  .description("Last odometer values")
                  .keywords(new LinkedHashSet<>(
                          List.of(
                              Attribute.builder()
                                  .name("provider:routing:alias")
                                  .value("metric:odometer").build(),
                              Attribute.builder()
                                  .name("scope")
                                  .value("metric:odometer").build(),
                              Attribute.builder()
                                  .name("event_usage").build()
                          )
                      )
                  ).url(
                      "/connectedasset/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/status")
                  .queryParams(new ArrayList<>(List.of(
                      QueryParam.builder().key("profile").value("fleet").build(),
                      QueryParam.builder().key("extension").value("odometer").build()
                  )))
                  .build()
          ))
      ).build();
  public static final String datasourceJson = TestResources
      .readContent("/datasource/model/web/certificateBasedBasicAuthDatasource.json");

  private CertificateBasedBasicAuthDatasourceFeatures() {
  }

  public static WebServerResource getResourceByAlias(String alias) {
    return CertificateBasedBasicAuthDatasourceFeatures.datasource
        .getResources().stream()
        .filter(r -> r.findFirstKeywordValueByName(ATTR_NAME_ALIAS)
            .map(a -> StringUtils.equals(a, alias))
            .orElse(false)
        )
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException(
                "No resource having a keyword name=" + ATTR_NAME_ALIAS + ", value=" + alias
                    + " found in webServerResource"));
  }

  public static WebServerResource getResourceHavingKeywordName(String name) {
    return CertificateBasedBasicAuthDatasourceFeatures.datasource
        .getResources().stream()
        .filter(r -> r.keywordsContainsName(name))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException(
                "No resource having a keyword name=" + name + " found in webServerResource"));
  }

}
