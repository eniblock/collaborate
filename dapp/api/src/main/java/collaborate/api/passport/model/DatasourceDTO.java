package collaborate.api.passport.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasourceDTO {

  private String id;
  private String assetIdForDatasource;
  private String baseUri;
  private Set<String> scopes;

}
