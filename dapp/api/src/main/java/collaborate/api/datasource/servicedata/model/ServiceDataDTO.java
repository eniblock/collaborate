package collaborate.api.datasource.servicedata.model;

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

  @Schema(
      description = "A simple name describing this datasource",
      example = "DSPConsortium1 digital-passports")
  protected String name;
  
  /**
   * DB inheritance field
   */
  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected String description;

  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  protected String scope;
}

