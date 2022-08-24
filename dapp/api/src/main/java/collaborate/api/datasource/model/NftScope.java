package collaborate.api.datasource.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class NftScope {

  @EmbeddedId
  private NFTScopeId nftScopeId;
  private String scope;
  @Column(unique = true)
  private Integer nftId;

  @JsonIgnore
  public String getDatasourceId() {
    return nftScopeId.getDatasource();
  }

}
