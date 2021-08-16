package collaborate.api.datasource.domain.web;

import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.authentication.Authentication;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import java.net.URI;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class WebServerDatasource extends DataSource {

  private URI baseUrl;

  @OneToMany(cascade = CascadeType.ALL)
  private List<WebServerResource> resources;

  @Builder(toBuilder = true)
  public WebServerDatasource(
      Long id,
      String name,
      DatasourceStatus status,
      Authentication authMethod,
      List<String> keywords,
      URI baseUrl,
      List<WebServerResource> resources) {
    super(id, name, status, authMethod, keywords);
    this.baseUrl = baseUrl;
    this.resources = resources;
  }

  @Override
  public String getType(){
    return "WEBSERVER";
  }
}
