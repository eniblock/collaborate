package collaborate.api.datasource.traefik.mapping;

import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.domain.DatasourceVisitor;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.WebServerResource;
import collaborate.api.datasource.traefik.routing.DatasourceNameSupplier;
import collaborate.api.datasource.traefik.routing.FromRoutePrefixSupplier;
import collaborate.api.datasource.traefik.routing.FromRouteRegexSupplier;
import collaborate.api.datasource.traefik.routing.RoutingKeyKeywordSupplier;
import collaborate.api.traefik.domain.Http;
import collaborate.api.traefik.mapping.MiddlewareFactory;
import collaborate.api.traefik.mapping.RouterFactory;
import collaborate.api.traefik.mapping.ServiceFactory;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DatasourceToHttpVisitor implements DatasourceVisitor {

  private final String certificatesPath;
  private final MiddlewareFactory middlewareFactory;
  private final RouterFactory routerFactory;
  private final ServiceFactory serviceFactory;

  private Http http = new Http();

  @Override
  public void visitWebServerDatasource(WebServerDatasource datasource) throws Exception {
    if (datasource.getResources() == null) {
      return;
    }
    var datasourceNameSupplier = new DatasourceNameSupplier(datasource);
    var datasourceKey = datasourceNameSupplier.get();
    var httpAuthenticationVisitor =
        initHttpAuthenticationVisitor(datasourceNameSupplier, datasource);
    String serverTransportName = initServerTransport(datasourceKey, httpAuthenticationVisitor);

    var service = serviceFactory.create(datasource.getBaseUrl(), serverTransportName);
    http.getServices().put(datasourceKey, service);

    for (WebServerResource resource : datasource.getResources()) {
      var resourceKey = datasourceKey
          + "-" + new RoutingKeyKeywordSupplier(resource.getKeywords()).get();

      List<String> middlewareNames = initMiddlewares(
          httpAuthenticationVisitor,
          datasourceKey,
          resource,
          resourceKey);

      http.getRouters().put(resourceKey + "-router",
          routerFactory.create(
              new FromRoutePrefixSupplier(datasourceKey, resource).get(),
              datasourceKey,
              middlewareNames,
              serverTransportName != null));
    }
  }

  private List<String> initMiddlewares(HttpAuthenticationVisitor httpAuthenticationVisitor,
      String datasourceKey,
      WebServerResource resource,
      String resourceKey) {

    var middlewareNames = new ArrayList<>(httpAuthenticationVisitor.getMiddlewares().keySet());

    var queryParamsOpt = middlewareFactory.createQueryParamOption(
        httpAuthenticationVisitor.getQueryParams(), resource.getQueryParams());
    queryParamsOpt.ifPresent(
        queryParams -> http.getMiddlewares().put(resourceKey + "-query-params", queryParams)
    );

    middlewareNames.add(resourceKey + "-query-params");

    var replacePathRegex = middlewareFactory.createReplacePathRegex(
        new FromRouteRegexSupplier(datasourceKey, resource).get(),
        Paths.get(
            new FromRoutePrefixSupplier(datasourceKey, resource).get(),
            resource.getUrl()
        ).toString()
    );
    http.getMiddlewares().put(resourceKey + "-replace-path-regex", replacePathRegex);
    middlewareNames.add(resourceKey + "-replace-path-regex");

    var stripFromRoutePrefix = middlewareFactory.createStripPrefix(
        new FromRoutePrefixSupplier(datasourceKey, resource).get()
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
      DatasourceNameSupplier datasourceNameSupplier, Datasource dataSource) throws Exception {
    var httpAuthenticationVisitor = new HttpAuthenticationVisitor(datasourceNameSupplier,
        certificatesPath);
    dataSource.getAuthMethod().accept(httpAuthenticationVisitor);

    http.getMiddlewares().putAll(httpAuthenticationVisitor.getMiddlewares());
    return httpAuthenticationVisitor;
  }

}
