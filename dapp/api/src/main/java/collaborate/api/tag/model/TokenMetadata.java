package collaborate.api.tag.model;

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

  // "" empty string key is the TZip-16 key name for storing the metadata URI value
  public static final String TOKEN_METADATA_FIELD = "";
  private Integer tokenId;

  public List<TagEntry<String, Bytes>> tokenInfo;

  @JsonIgnore
  public String getIpfsUri() {
    return tokenInfo.stream()
        .filter(tagEntry -> TOKEN_METADATA_FIELD.equals(tagEntry.getKey()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Can't find metadata field for tokenId=" + tokenId)
        ).getValue()
        .toString();
  }

}
