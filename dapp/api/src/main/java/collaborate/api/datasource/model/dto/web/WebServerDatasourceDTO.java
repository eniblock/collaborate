package collaborate.api.datasource.model.dto.web;

import static java.lang.String.format;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WebServerDatasourceDTO extends DatasourceDTO {

  @NotEmpty
  @HasAssetListConstraint
  @Valid
  @Schema(description = "The resources definition this datasource exposes")
  private List<WebServerResource> resources;

  @NotNull
  @Schema(description = "The base URL used as a prefix of each resource URL", example = "https://passports.dspconsortium1.com")
  private String baseUrl;

  @Builder(toBuilder = true)
  public WebServerDatasourceDTO(
      UUID id,
      String name,
      Authentication authMethod,
      HashSet<String> keywords,
      String type,
      List<WebServerResource> resources,
      String baseUrl
  ) {
    super(id, name, authMethod, keywords, type);
    this.baseUrl = baseUrl;
    this.resources = resources;
  }

  @Override
  public <T> T accept(DatasourceDTOVisitor<T> visitor) throws DatasourceVisitorException {
    return visitor.visitWebServerDatasource(this);
  }


  public Optional<WebServerResource> findResourceByKeyword(String keyword) {
    Optional<WebServerResource> firstMatchingResource = Optional.empty();
    if (resources != null) {
      firstMatchingResource = resources.stream()
          .filter(r -> r.keywordsContainsName(keyword))
          .findFirst();
    }
    return firstMatchingResource;
  }

  public WebServerResource getResourceByKeywordOrThrow(String keyword) {
    Optional<WebServerResource> firstMatchingResource = findResourceByKeyword(keyword);
    if (firstMatchingResource.isEmpty()) {
      throw new IllegalStateException(
          format("keyword={%s} not found for datasource={%s}", keyword, this));
    }
    return firstMatchingResource.get();
  }

}
