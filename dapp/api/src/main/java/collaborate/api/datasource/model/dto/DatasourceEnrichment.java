package collaborate.api.datasource.model.dto;

import collaborate.api.datasource.model.Metadata;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasourceEnrichment<D extends DatasourceDTO> {

  private D datasource;
  private Set<Metadata> metadata;
}
