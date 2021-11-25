package collaborate.api.datasource.nft.model.metadata;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDataCatalog {

  private List<DatasourceLink> datasources;

}
