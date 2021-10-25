package collaborate.api.datasource.model.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DatasourceVisitorException extends Exception {

  public DatasourceVisitorException(Exception e) {
    super(e);
  }
}
