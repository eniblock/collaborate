package collaborate.api.datasource.servicedata.find;

import collaborate.api.datasource.servicedata.model.ServiceData;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetServiceDataCatalogDTO {

  @Schema(description = "The list of services")
  @NotEmpty
  private List<@Valid ServiceData> services;

}
