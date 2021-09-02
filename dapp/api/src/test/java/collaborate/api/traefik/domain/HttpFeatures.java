package collaborate.api.traefik.domain;

import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.domain.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.traefik.domain.middleware.Headers;
import collaborate.api.traefik.domain.middleware.Middleware;
import collaborate.api.traefik.domain.middleware.RedirectRegex;
import collaborate.api.traefik.domain.middleware.ReplacePathRegex;
import collaborate.api.traefik.domain.middleware.StripPrefix;
import collaborate.api.traefik.domain.servertransport.Certificates;
import collaborate.api.traefik.domain.servertransport.ServersTransport;
import collaborate.api.traefik.domain.service.LoadBalancer;
import collaborate.api.traefik.domain.service.Server;
import collaborate.api.traefik.domain.service.Service;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class HttpFeatures {

  private static final HttpFeatures INSTANCE = new HttpFeatures();

  public static final EntryPoint entryPoint = EntryPoint.builder()
      .http(Http.builder()
          .routers(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-vehicles-router", Router.builder()
                  .entryPoints(List.of("websecure"))
                  .service("525003f6-f85f-11eb-9a03-0242ac130003")
                    .rule("PathPrefix(`/datasource/525003f6-f85f-11eb-9a03-0242ac130003/vehicles`)")
                  .middlewares(List.of(
                      "525003f6-f85f-11eb-9a03-0242ac130003-auth-headers",
                      "525003f6-f85f-11eb-9a03-0242ac130003-vehicles-query-params",
                      "525003f6-f85f-11eb-9a03-0242ac130003-vehicles-replace-path-regex",
                      "525003f6-f85f-11eb-9a03-0242ac130003-vehicles-strip-prefix"
                  )).tls(true)
                  .build()),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-kilometer-router", Router.builder()
                  .entryPoints(List.of("websecure"))
                  .service("525003f6-f85f-11eb-9a03-0242ac130003")
                  .rule("PathPrefix(`/datasource/525003f6-f85f-11eb-9a03-0242ac130003/kilometer`)")
                  .middlewares(List.of(
                      "525003f6-f85f-11eb-9a03-0242ac130003-auth-headers",
                      "525003f6-f85f-11eb-9a03-0242ac130003-kilometer-query-params",
                      "525003f6-f85f-11eb-9a03-0242ac130003-kilometer-replace-path-regex",
                      "525003f6-f85f-11eb-9a03-0242ac130003-kilometer-strip-prefix"
                  )).tls(true)
                  .build()),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-maintenance-router", Router.builder()
                  .entryPoints(List.of("websecure"))
                  .service("525003f6-f85f-11eb-9a03-0242ac130003")
                  .rule("PathPrefix(`/datasource/525003f6-f85f-11eb-9a03-0242ac130003/maintenance`)")
                  .middlewares(List.of(
                      "525003f6-f85f-11eb-9a03-0242ac130003-auth-headers",
                      "525003f6-f85f-11eb-9a03-0242ac130003-maintenance-query-params",
                      "525003f6-f85f-11eb-9a03-0242ac130003-maintenance-replace-path-regex",
                      "525003f6-f85f-11eb-9a03-0242ac130003-maintenance-strip-prefix"
                  )).tls(true)
                  .build())
          ))
          .middlewares(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-auth-headers", Middleware.builder()
                  .headers(
                      Headers.builder()
                          .customRequestHeaders(Map.ofEntries(
                              new SimpleEntry<>("Authorization", "Basic TVdQRFJWMDE6QkJybEtRMGk=")
                          ))
                          .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-vehicles-query-params", Middleware.builder()
                  .redirectRegex(RedirectRegex.builder()
                      .regex("/([^\\?\\s]*)(\\??)(.*)")
                      .replacement("/${1}?client_id=c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
                      .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-kilometer-query-params", Middleware.builder()
                  .redirectRegex(RedirectRegex.builder()
                      .regex("/([^\\?\\s]*)(\\??)(.*)")
                      .replacement("/${1}?client_id=c8fc43d5-f43f-44e5-acc2-b8aebaee90e2&profile=fleet&extension=odometer")
                      .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-maintenance-query-params", Middleware.builder()
                  .redirectRegex(RedirectRegex.builder()
                      .regex("/([^\\?\\s]*)(\\??)(.*)")
                      .replacement("/${1}?client_id=c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
                      .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-vehicles-strip-prefix", Middleware.builder()
                  .stripPrefix(StripPrefix.builder()
                      .prefixes(List.of("/datasource/525003f6-f85f-11eb-9a03-0242ac130003/vehicles"))
                      .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-kilometer-strip-prefix", Middleware.builder()
                  .stripPrefix(StripPrefix.builder()
                      .prefixes(List.of("/datasource/525003f6-f85f-11eb-9a03-0242ac130003/kilometer"))
                      .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-maintenance-strip-prefix", Middleware.builder()
                  .stripPrefix(StripPrefix.builder()
                      .prefixes(List.of("/datasource/525003f6-f85f-11eb-9a03-0242ac130003/maintenance"))
                      .build()
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-vehicles-replace-path-regex", Middleware.builder()
                  .replacePathRegex(new ReplacePathRegex(
                      "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/vehicles",
                      "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/vehicles/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles")
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-kilometer-replace-path-regex", Middleware.builder()
                  .replacePathRegex(new ReplacePathRegex(
                      "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/kilometer/(.*)",
                      "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/kilometer/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/status")
                  ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-maintenance-replace-path-regex", Middleware.builder()
                  .replacePathRegex(new ReplacePathRegex(
                      "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/maintenance/(.*)/(.*)",
                      "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/maintenance/connectedcar/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/maintenance/$2")
                  ).build()
              )
          ))
          .services(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003", Service.builder()
                  .loadBalancer(LoadBalancer.builder()
                      .servers(List.of(new Server("https://api-cert-preprod.groupe-psa.com")))
                      .passHostHeader(false)
                      .serversTransport("525003f6-f85f-11eb-9a03-0242ac130003-serversTransport")
                      .build())
                  .build())
          ))
          .serversTransports(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-serversTransport", ServersTransport.builder()
                  .insecureSkipVerify(false)
                  .certificates(List.of(Certificates.builder()
                      .certFile("/ssl/certs/525003f6-f85f-11eb-9a03-0242ac130003.cer")
                      .keyFile("/ssl/certs/525003f6-f85f-11eb-9a03-0242ac130003.pem")
                      .build()))
                  .build())
          )).build()
      ).build();

  public final String entryPointYaml;

  public HttpFeatures() {
      try {
        entryPointYaml = IOUtils.toString(
            Objects.requireNonNull(
                CertificateBasedBasicAuthDatasourceFeatures.class
                    .getResourceAsStream(
                        "/traefik/domain/entrypoint/entrypoint.yml")
            ), UTF_8.name()
        );
      } catch (IOException e) {
        throw new IllegalStateException("Can't read entrypoint.yml");
      }
  }

  public static HttpFeatures getInstance() {
    return INSTANCE;
  }

}
