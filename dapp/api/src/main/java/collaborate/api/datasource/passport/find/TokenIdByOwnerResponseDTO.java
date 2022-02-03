package collaborate.api.datasource.passport.find;

import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenIdByOwnerResponseDTO {

  @JsonProperty("tokens_by_owner")
  private List<TagEntry<String, HashMap<String, Integer>>> tokensByOwner;

}
