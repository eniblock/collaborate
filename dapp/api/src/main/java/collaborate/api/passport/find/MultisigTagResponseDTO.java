package collaborate.api.passport.find;

import collaborate.api.nft.model.storage.Multisig;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultisigTagResponseDTO {

  @JsonProperty("multisigs")
  private List<TagEntry<Integer, Multisig>> multisigs;

}
