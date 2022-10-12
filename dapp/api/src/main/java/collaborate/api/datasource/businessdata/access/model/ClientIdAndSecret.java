package collaborate.api.datasource.businessdata.access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClientIdAndSecret {

  private String clientId;
  private String clientSecret;
}
