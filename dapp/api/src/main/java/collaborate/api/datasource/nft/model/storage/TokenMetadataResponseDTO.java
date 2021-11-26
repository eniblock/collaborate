package collaborate.api.datasource.nft.model.storage;

import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenMetadataResponseDTO {

  private List<TagEntry<Integer, TokenMetadata>> tokenMetadata;

}
