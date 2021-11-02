package collaborate.api.passport.metric;

import collaborate.api.datasource.model.Metadata;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricGatewayDTO {

  private String scope;
  private String uri;
  private Set<Metadata> metadata = new HashSet<>();
}
