package collaborate.api.datasource.serviceconsent.model.transaction;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class MintTransactionParameters {

  private Long amount;

  @JsonProperty("address")
  private String owner;

  private Map<String, Bytes> metadata;

  private Long tokenId;
  
  private Long passportId;

  public String getIpfsMetadataURI() {
    return metadata.get("").toString();
  }

}
