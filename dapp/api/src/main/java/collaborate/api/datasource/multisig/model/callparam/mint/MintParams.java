package collaborate.api.datasource.multisig.model.callparam.mint;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MintParams {

  private long amount;

  private String address;

  private Map<String, Bytes> metadata;

  public Bytes getIpfsMetadata(){
    return metadata.get("");
  }

}
