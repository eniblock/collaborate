package collaborate.api.datasource.servicedata.find;

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
public class OwnerByTokenIdResponseDTO {

  @JsonProperty("owner_by_token_id")
  private List<TagEntry<Integer, String>> ownerBuTokenId;

}
