package collaborate.api.datasource.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class AssetScope {

  @EmbeddedId
  private AssetScopeId assetScopeId;
  private String scope;
  @Column(unique = true)
  private Integer nftId;

}
