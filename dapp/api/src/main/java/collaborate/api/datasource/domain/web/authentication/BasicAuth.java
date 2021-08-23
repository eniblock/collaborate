package collaborate.api.datasource.domain.web.authentication;

import collaborate.api.datasource.domain.web.QueryParam;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class BasicAuth extends Authentication {

  @Transient
  protected String user;
  @ToString.Exclude
  @Transient
  protected String password;

  @OneToMany(cascade = CascadeType.ALL)
  protected List<QueryParam> queryParams;

  public BasicAuth(String user, String password,
      List<QueryParam> queryParams) {
    this.user = user;
    this.password = password;
    this.queryParams = queryParams;
  }

  @Override
  public void accept(AuthenticationVisitor visitor) {
    visitor.visitBasicAuth(this);
  }
}
