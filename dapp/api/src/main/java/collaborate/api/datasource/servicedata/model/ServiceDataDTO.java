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
public class ServiceDataDTO implements Serializable  {

  protected UUID id;

  @Schema(description = "A simple name describing the service", example = "DSPConsortium1 digital-passports")
  protected String name;
  
  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected String description;

  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected UUID datasource;

  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected String scope;

  public <T> T accept(ServiceDataDTOVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

