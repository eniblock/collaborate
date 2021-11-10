package collaborate.api.passport.model;

import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasourceDTO {

  @NotNull
  private String id;
  @NotNull
  private String assetIdForDatasource;
  private String baseUri;
  private Set<String> scopes;

}
