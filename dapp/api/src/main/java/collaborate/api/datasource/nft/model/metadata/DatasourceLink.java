package collaborate.api.datasource.nft.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceLink {

  private String id;

  private String uri;

  private String assetIdForDatasource;

}
