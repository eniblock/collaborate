package collaborate.api.datasource.model.dto;

import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;

public interface DatasourceDTOVisitor {

  void visitWebServerDatasource(WebServerDatasourceDTO webServerResource) throws Exception;
}
