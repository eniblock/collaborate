package collaborate.api.passport.model.storage;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PassportsIndexer {

  private List<PassportsIndexerToken> tokens;

  private List<Integer> unsignedMultisigs;
}