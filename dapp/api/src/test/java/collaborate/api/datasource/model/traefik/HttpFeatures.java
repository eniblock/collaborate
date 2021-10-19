package collaborate.api.datasource.model.traefik;

import collaborate.api.datasource.model.traefik.middleware.Headers;
import collaborate.api.datasource.model.traefik.middleware.Middleware;
import collaborate.api.datasource.model.traefik.middleware.RedirectRegex;
import collaborate.api.datasource.model.traefik.middleware.ReplacePathRegex;
import collaborate.api.datasource.model.traefik.middleware.StripPrefix;
import collaborate.api.datasource.model.traefik.servertransport.Certificates;
import collaborate.api.datasource.model.traefik.servertransport.ServersTransport;
import collaborate.api.datasource.model.traefik.service.LoadBalancer;
import collaborate.api.datasource.model.traefik.service.Server;
import collaborate.api.datasource.model.traefik.service.Service;
import collaborate.api.test.TestResources;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

public class HttpFeatures {

  public static final TraefikProviderConfiguration PROVIDER_CONFIGURATION = TraefikProviderConfiguration
      .builder()
      .http(Http.builder()
          .routers(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-router",
                  Router.builder()
                      .entryPoints(List.of("websecure"))
                      .service("525003f6-f85f-11eb-9a03-0242ac130003")
                      .rule(
                          "PathPrefix(`/datasource/525003f6-f85f-11eb-9a03-0242ac130003/purpose:test-connection`)")
                      .middlewares(List.of(
                          "525003f6-f85f-11eb-9a03-0242ac130003-auth-headers",
                          "525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-query-params",
                          "525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-replace-path-regex",
                          "525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-strip-prefix"
                      )).tls(true)
                      .build()),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-router",
                  Router.builder()
                      .entryPoints(List.of("websecure"))
                      .service("525003f6-f85f-11eb-9a03-0242ac130003")
                      .rule(
                          "PathPrefix(`/datasource/525003f6-f85f-11eb-9a03-0242ac130003/scope:metric:odometer`)")
                      .middlewares(List.of(
                          "525003f6-f85f-11eb-9a03-0242ac130003-auth-headers",
                          "525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-query-params",
                          "525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-replace-path-regex",
                          "525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-strip-prefix"
                      )).tls(true)
                      .build())
          ))
          .middlewares(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-auth-headers",
                  Middleware.builder()
                      .headers(
                          Headers.builder()
                              .customRequestHeaders(Map.ofEntries(
                                  new SimpleEntry<>("Authorization",
                                      "Basic TVdQRFJWMDE6QkJybEtRMGk=")
                              ))
                              .build()
                      ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-query-params",
                  Middleware.builder()
                      .redirectRegex(RedirectRegex.builder()
                          .regex("/([^\\?\\s]*)(\\??)(.*)")
                          .replacement("/${1}?client_id=c8fc43d5-f43f-44e5-acc2-b8aebaee90e2")
                          .build()
                      ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-query-params",
                  Middleware.builder()
                      .redirectRegex(RedirectRegex.builder()
                          .regex("/([^\\?\\s]*)(\\??)(.*)")
                          .replacement(
                              "/${1}?client_id=c8fc43d5-f43f-44e5-acc2-b8aebaee90e2&profile=fleet&extension=odometer")
                          .build()
                      ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-strip-prefix",
                  Middleware.builder()
                      .stripPrefix(StripPrefix.builder()
                          .prefixes(
                              List.of("/datasource/525003f6-f85f-11eb-9a03-0242ac130003/purpose:test-connection"))
                          .build()
                      ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-strip-prefix",
                  Middleware.builder()
                      .stripPrefix(StripPrefix.builder()
                          .prefixes(
                              List.of("/datasource/525003f6-f85f-11eb-9a03-0242ac130003/scope:metric:odometer"))
                          .build()
                      ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-purpose:test-connection-replace-path-regex",
                  Middleware.builder()
                      .replacePathRegex(new ReplacePathRegex(
                          "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/purpose:test-connection",
                          "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/purpose:test-connection/connectedasset/v3/fleets/5fb2830db35c87031c2e0d68/vehicles")
                      ).build()
              ),
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-scope:metric:odometer-replace-path-regex",
                  Middleware.builder()
                      .replacePathRegex(new ReplacePathRegex(
                          "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/scope:metric:odometer/(.*)",
                          "/datasource/525003f6-f85f-11eb-9a03-0242ac130003/scope:metric:odometer/connectedasset/v3/fleets/5fb2830db35c87031c2e0d68/vehicles/$1/status")
                      ).build()
              )
          ))
          .services(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003", Service.builder()
                  .loadBalancer(LoadBalancer.builder()
                      .servers(List.of(new Server("https://api-cert-preprod.dspconsortium1.com")))
                      .passHostHeader(false)
                      .serversTransport("525003f6-f85f-11eb-9a03-0242ac130003-serversTransport")
                      .build())
                  .build())
          ))
          .serversTransports(Map.ofEntries(
              new SimpleEntry<>("525003f6-f85f-11eb-9a03-0242ac130003-serversTransport",
                  ServersTransport.builder()
                      .insecureSkipVerify(false)
                      .certificates(List.of(Certificates.builder()
                          .certFile("/ssl/certs/525003f6-f85f-11eb-9a03-0242ac130003.crt")
                          .keyFile("/ssl/certs/525003f6-f85f-11eb-9a03-0242ac130003.key")
                          .build()))
                      .build())
          )).build()
      ).build();

  public static final String entryPointYaml  = TestResources.readPath(
      "/datasource/domain/traefik/entrypoint.yml");

  private HttpFeatures() {
  }
}
