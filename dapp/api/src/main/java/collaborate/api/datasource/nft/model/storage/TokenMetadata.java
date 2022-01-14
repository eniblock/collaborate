package collaborate.api.datasource.nft.model.storage;

import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TokenMetadata {

  private Integer tokenId;

  private List<TagEntry<String, Bytes>> tokenInfo;

  @JsonIgnore
  public String getIpfsUri() {
    return tokenInfo.stream()
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Can't find metadata field for tokenId=" + tokenId))
        .getValue()
        .toString();
  }

}
