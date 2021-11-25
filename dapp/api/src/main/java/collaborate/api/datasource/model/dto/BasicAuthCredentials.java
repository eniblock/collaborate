package collaborate.api.datasource.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAuthCredentials implements Serializable {

  private String user;
  @ToString.Exclude
  private String password;
}
