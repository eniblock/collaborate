package collaborate.api.datasource.businessdata.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetIdAndUri {

  private String assetId;
  private String uri;
}
