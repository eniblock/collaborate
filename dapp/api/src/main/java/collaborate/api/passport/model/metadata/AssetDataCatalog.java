package collaborate.api.passport.model.metadata;

import collaborate.api.passport.create.DatasourceLink;
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
