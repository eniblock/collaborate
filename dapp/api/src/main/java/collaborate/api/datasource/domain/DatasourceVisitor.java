package collaborate.api.datasource.domain;

import collaborate.api.datasource.domain.web.WebServerDatasource;

public interface DatasourceVisitor {

  void visitWebServerDatasource(WebServerDatasource webServerResource) throws Exception;
}
