package collaborate.api.passport.model.storage;

import collaborate.api.nft.model.storage.TokenIndex;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PassportsIndexer {

  private List<TokenIndex> tokens;

  private List<Integer> unsignedMultisigs;
}
