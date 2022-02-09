package collaborate.api.datasource.passport.model.transaction;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class Fa2TransactionParameters {

  private Long amount;

  private String address;

  private Map<String, Bytes> metadata;

  private Long tokenId;

  public String getIpfsMetadataURI() {
    return metadata.get("").toString();
  }

}
