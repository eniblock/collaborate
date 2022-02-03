package collaborate.api.tag.model.proxytokencontroller;

import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties({"entry_point"})
public class MultisigBuildCallParamMint extends MultisigBuildCallParam {
  
  public String getEntryPoint() {
    return "mint";
  }

  private String targetAddress;

  private MultisigBuildCallParamMintDetails parameters;

}
