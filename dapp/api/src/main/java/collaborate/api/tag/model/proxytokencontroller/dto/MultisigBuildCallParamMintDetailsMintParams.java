package collaborate.api.tag.model.proxytokencontroller.dto;

import collaborate.api.tag.model.proxytokencontroller.MultisigMetadata;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MultisigBuildCallParamMintDetailsMintParams {

  private long amount;
  private String address;
  private List<MultisigMetadata> metadata;
}
