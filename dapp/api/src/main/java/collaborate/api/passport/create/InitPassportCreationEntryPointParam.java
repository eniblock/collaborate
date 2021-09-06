package collaborate.api.passport.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitPassportCreationEntryPointParam {

  private String vehicleOwnerAddress;
  private String dspAddress;
  private String vin;
  @JsonProperty("datasource_uuid")
  private String datasourceUUID;

}
