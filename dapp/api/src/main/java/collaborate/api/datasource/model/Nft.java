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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
public class Nft {

  @EmbeddedId
  private AssetId assetId;
  @Column(unique = true)
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
  }

}
