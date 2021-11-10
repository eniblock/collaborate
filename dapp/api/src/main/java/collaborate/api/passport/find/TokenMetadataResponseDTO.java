package collaborate.api.passport.find;

import collaborate.api.passport.model.storage.TokenMetadata;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TokenMetadataResponseDTO {

  private List<TagEntry<Integer, TokenMetadata>> tokenMetadata;

}
