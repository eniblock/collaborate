package collaborate.api.tag.model.proxytokencontroller;

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
public class MultisigBuildParam <E extends MultisigBuildCallParam> {

  private String buildAndSign;

  private E callParams;

  private long multisigId;

  private List<String> signers;

}
