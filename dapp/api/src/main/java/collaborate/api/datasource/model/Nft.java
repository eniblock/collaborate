package collaborate.api.datasource.model;

import static collaborate.api.datasource.model.dto.web.Attribute.ATTR_JWT_SCOPE;

import collaborate.api.datasource.passport.model.TokenStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import collaborate.api.tag.BytesDeserializer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Slf4j
public class Nft {

  @EmbeddedId
  private AssetId assetId;
  
  //@Column(unique = true)
  private Integer nftId;
  
  private String ownerAddress;
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode metadata;
  private TokenStatus status;


  public Nft(AssetId assetId) {
    this.assetId = assetId;
  }

  @JsonIgnore
  public String getDatasourceId() {
    return assetId.getDatasource();
  }

  @JsonIgnore
  public Optional<String> findScope() {
    if (metadata.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(metadata.get(ATTR_JWT_SCOPE))
        .map(JsonNode::asText);
        //.map(s -> {  log.info(s); return s.length() > 0 ? new String(BytesDeserializer.decodeHexString(s), StandardCharsets.UTF_8) : ""; });
  }

}
