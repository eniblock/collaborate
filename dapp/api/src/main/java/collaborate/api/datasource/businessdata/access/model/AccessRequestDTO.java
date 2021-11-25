package collaborate.api.datasource.businessdata.access.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequestDTO {

  @NotNull
  private Integer tokenId;
  @NotBlank
  private String datasourceId;
  @NotBlank
  private String assetIdForDatasource;
  @NotBlank
  private String providerAddress;
}
