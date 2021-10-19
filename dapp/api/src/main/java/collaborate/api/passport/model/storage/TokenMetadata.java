package collaborate.api.passport.model.storage;

import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TezosMap;
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

  public Bytes getIpfsUri() {
    return tokenInfo.findValue("").orElseThrow(
        () -> new IllegalStateException("Can't find metadata field for tokenId=" + tokenId));
  }

}