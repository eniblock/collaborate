package collaborate.api.passport.create.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitPassportCreationParams implements Serializable {

  @NotNull
  private String vehicleOwnerAddress;

  @NotNull
  private String dspAddress;

  @NotNull
  private String vin;

  @NotNull
  @JsonProperty("datasource_uuid")
  private String datasourceUUID;

}
