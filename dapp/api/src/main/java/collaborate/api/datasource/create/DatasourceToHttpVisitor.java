package collaborate.api.datasource.create;

import collaborate.api.datasource.create.provider.traefik.HttpAuthenticationVisitor;
import collaborate.api.datasource.create.provider.traefik.MiddlewareFactory;
import collaborate.api.datasource.create.provider.traefik.RouterFactory;
import collaborate.api.datasource.create.provider.traefik.ServiceFactory;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.traefik.Http;
import collaborate.api.datasource.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.traefik.routing.DatasourceKeySupplier;
import collaborate.api.datasource.traefik.routing.FromRoutePrefixSupplier;
import collaborate.api.datasource.traefik.routing.FromRouteRegexSupplier;
import collaborate.api.datasource.traefik.routing.RoutingKeyFromKeywordSupplier;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DatasourceToHttpVisitor implements DatasourceDTOVisitor<Void> {

  private final String certificatesPath;
  private final MiddlewareFactory middlewareFactory;
  private final RouterFactory routerFactory;
  private final ServiceFactory serviceFactory;

  private Http http = new Http();

  @Override
  public Void visitWebServerDatasource(WebServerDatasourceDTO datasource) {
    if (datasource.getResources() == null) {
      return null;
    }
    var datasourceNameSupplier = new DatasourceKeySupplier(datasource);
    var datasourceKey = datasourceNameSupplier.get();
    var httpAuthenticationVisitor =
        initHttpAuthenticationVisitor(datasourceNameSupplier, datasource);
    String serverTransportName = initServerTransport(datasourceKey, httpAuthenticationVisitor);

    var service = serviceFactory.create(datasource.getBaseUrl(), serverTransportName);
    http.getServices().put(datasourceKey, service);

    for (WebServerResource resource : datasource.getResources()) {
      var resourceKey = datasourceKey
          + "-" + new RoutingKeyFromKeywordSupplier(resource.getKeywords()).get();

      List<String> middlewareNames = initMiddlewares(
          httpAuthenticationVisitor,
          datasourceKey,
          resource,
          resourceKey);

      http.getRouters().put(resourceKey + "-router",
          routerFactory.create(
              new FromRoutePrefixSupplier(datasourceKey, resource.getKeywords()).get(),
              datasourceKey,
              middlewareNames,
              serverTransportName != null));
    }
    return null;
  }

  private List<String> initMiddlewares(HttpAuthenticationVisitor httpAuthenticationVisitor,
      String datasourceKey,
      WebServerResource resource,
      String resourceKey) {

    var middlewareNames = new ArrayList<>(httpAuthenticationVisitor.getMiddlewares().keySet());

    var queryParamsOpt = middlewareFactory.createQueryParamOption(
        httpAuthenticationVisitor.getQueryParams(), resource.getQueryParams()
    );

    queryParamsOpt.ifPresent(
        queryParams -> {
          http.getMiddlewares().put(resourceKey + "-query-params", queryParams);
          middlewareNames.add(resourceKey + "-query-params");
        }
    );

    var replacePathRegex = middlewareFactory.createReplacePathRegex(
        new FromRouteRegexSupplier(datasourceKey, resource).get(),
        Paths.get(
            new FromRoutePrefixSupplier(datasourceKey, resource.getKeywords()).get(),
            resource.getUrl()
        ).toString()
    );
    http.getMiddlewares().put(resourceKey + "-replace-path-regex", replacePathRegex);
    middlewareNames.add(resourceKey + "-replace-path-regex");

    var stripFromRoutePrefix = middlewareFactory.createStripPrefix(
        new FromRoutePrefixSupplier(datasourceKey, resource.getKeywords()).get()
    );
    http.getMiddlewares().put(resourceKey + "-strip-prefix", stripFromRoutePrefix);
    middlewareNames.add(resourceKey + "-strip-prefix");

    return middlewareNames;
  }

  private String initServerTransport(String datasourceName,
      HttpAuthenticationVisitor httpAuthenticationVisitor) {
    String serverTransportName = null;
    if (httpAuthenticationVisitor.getServersTransport() != null) {
      serverTransportName = datasourceName + "-serversTransport";
      http.getServersTransports().put(
          serverTransportName,
          httpAuthenticationVisitor.getServersTransport());
    }
    return serverTransportName;
  }

  private HttpAuthenticationVisitor initHttpAuthenticationVisitor(
      DatasourceKeySupplier datasourceKeySupplier, DatasourceDTO dataSource) {
    var authHeaderKeySupplier = new AuthHeaderKeySupplier(datasourceKeySupplier);
    var httpAuthenticationVisitor = new HttpAuthenticationVisitor(
        authHeaderKeySupplier,
        datasourceKeySupplier,
        certificatesPath
    );

    dataSource.getAuthMethod().accept(httpAuthenticationVisitor);

    http.getMiddlewares().putAll(httpAuthenticationVisitor.getMiddlewares());
    return httpAuthenticationVisitor;
  }

}
