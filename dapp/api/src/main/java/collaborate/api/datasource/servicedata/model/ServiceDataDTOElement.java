package collaborate.api.datasource.servicedata.model;

import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDataDTOElement implements Serializable  {

  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected String datasource;

  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected String scope;

}

