package collaborate.api.datasource.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NFTScopeId implements Serializable {

  private String datasource;
  private String alias;

  public NFTScopeId(String rawId) {
    var unsplitted = StringUtils.split(rawId, ":");
    this.datasource = unsplitted[0];
    this.alias = unsplitted[1];
  }
}
