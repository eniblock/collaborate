package collaborate.api.datasource.model.dto;

import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;

public interface DatasourceDTOVisitor<T> {

  T visitWebServerDatasource(WebServerDatasourceDTO serverDatasourceDTO)
      throws DatasourceVisitorException;
}
