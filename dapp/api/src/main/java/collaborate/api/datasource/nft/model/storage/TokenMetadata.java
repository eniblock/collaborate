package collaborate.api.datasource.nft.model.storage;

import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TezosMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TokenMetadata {

  private Integer tokenId;

  private TezosMap<String, Bytes> tokenInfo;

  @JsonIgnore
  public String getIpfsUri() {
    return tokenInfo.findValue("")
        .map(Bytes::toString)
        .orElseThrow(
            () -> new IllegalStateException("Can't find metadata field for tokenId=" + tokenId));
  }

}
