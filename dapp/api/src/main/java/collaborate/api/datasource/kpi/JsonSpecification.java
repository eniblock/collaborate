package collaborate.api.datasource.kpi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonSpecification {

  private String jsonPath;
  private String searchedValue;

}
