package collaborate.api.datasource.create;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MintBusinessDataParamsDTO {

  private String nftOperatorAddress;
  private String assetId;
  private Bytes metadataUri;
}
