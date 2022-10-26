package collaborate.api.datasource.servicedata.model;

import collaborate.api.datasource.model.dto.DatasourceVisitorException;

public interface ServiceDataDTOVisitor<T> {

  T visit(ServiceDataDTO serverDatasourceDTO);
}
