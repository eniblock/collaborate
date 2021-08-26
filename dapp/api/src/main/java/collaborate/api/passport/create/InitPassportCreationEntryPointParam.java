package collaborate.api.passport.create;

import collaborate.api.tag.model.Bytes;
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
  private Bytes vin;
  @JsonProperty("datasource_uuid")
  private Bytes datasourceUUID;

}
