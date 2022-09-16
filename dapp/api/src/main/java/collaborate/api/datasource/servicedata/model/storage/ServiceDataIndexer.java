package collaborate.api.datasource.servicedata.model.storage;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServiceDataIndexer {

  private List<TokenIndex> tokens;

  private List<Integer> unsignedMultisigs;
}
