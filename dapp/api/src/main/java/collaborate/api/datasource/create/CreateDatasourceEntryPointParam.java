package collaborate.api.datasource.create;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDatasourceEntryPointParam {

  private UUID datasourceUUID;
  private String cid;

}
