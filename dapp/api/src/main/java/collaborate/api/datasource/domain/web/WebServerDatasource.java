package collaborate.api.datasource.domain.web;

import static java.lang.String.format;

import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.domain.DatasourceVisitor;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import collaborate.api.datasource.domain.web.authentication.Authentication;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class WebServerDatasource extends Datasource {

  @Column(nullable = false)
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "datasource_id")
  private List<WebServerResource> resources;

  @NotNull
  private String baseUrl;

  @Builder(toBuilder = true)
  public WebServerDatasource(
      UUID id,
      String name,
      DatasourceStatus status,
      Authentication authMethod,
      List<String> keywords,
      List<WebServerResource> resources,
      String baseUrl
  ) {
    super(id, name, status, authMethod, keywords);
    this.baseUrl = baseUrl;
    this.resources = resources;
  }

  @Override
  public void accept(DatasourceVisitor visitor) {
    visitor.visitWebServerDatasource(this);
  }

  public Optional<WebServerResource> findResourceByKeyword(String keyword) {
    Optional<WebServerResource> firstMatchingResource = Optional.empty();
    if (resources != null) {
      firstMatchingResource = resources.stream()
          .filter(r -> r.getKeywords() != null)
          .filter(r -> r.getKeywords().contains(keyword))
          .findFirst();
    }
    return firstMatchingResource;
  }

  public WebServerResource findResourceByKeywordOrThrow(String keyword) {
    Optional<WebServerResource> firstMatchingResource = findResourceByKeyword(keyword);
    if (firstMatchingResource.isEmpty()) {
      throw new IllegalStateException(
          format("keyword={%s} not found for datasource={%s}", keyword, this));
    }
    return firstMatchingResource.get();
  }
}
