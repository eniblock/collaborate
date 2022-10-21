package collaborate.api.datasource.serviceconsent.find;

import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.TagPair;
import collaborate.api.tag.model.storage.TagSet;
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
public class OperatorsByTokenIdsAndOwnersResponseDTO {

  @JsonProperty("operators_by_token")
  private List<TagEntry<TagPair<String, Integer>, TagSet<String>>> operatorsByToken;

}
