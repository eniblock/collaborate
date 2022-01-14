package collaborate.api.datasource.nft.model.storage;

import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CallParams {

  private String entryPoint;

  private MultisigBuildCallParamMintDetails parameters; // Object because there are many kind of parameters (for "mint", "set_pause", "transfer" ...)

  private String targetAddress;

  public String getOwnerAddressFromMultisig() {
    var mint = parameters.getMint();
    var mintParams = mint.getMintParams();
    return mintParams.getAddress();
  }

  public String getOperatorAddressFromMultisig() {
    var mint =  parameters.getMint();
    return mint.getOperator();
  }

  public Bytes getMetadataFromMultisig() {
    var mint =  parameters.getMint();
    var mintParams =  mint.getMintParams();
    var metadata = mintParams.getMetadata().get(0);
    return metadata.getValue();
  }
}
