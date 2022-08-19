package collaborate.api.datasource.model;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetScopeId {

  private String datasource;
  private String alias;

  public AssetScopeId(String rawId) {
    var unsplitted = StringUtils.split(rawId, ":");
    this.datasource = unsplitted[0];
    this.alias = unsplitted[1];
  }
}
