package collaborate.api.datasource.create;

import static collaborate.api.datasource.MetadataService.LIST_ASSET_SCOPE;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;

import collaborate.api.datasource.URIFactory;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.http.RequestEntityVisitorFactory;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestEntitySupplierFactory {

  private final RequestEntityVisitorFactory requestEntityVisitorFactory;
  private final URIFactory uriFactory;

  public Supplier<ResponseEntity<JsonNode>> create(WebServerDatasourceDTO serverDatasourceDTO,
      String resourceKeyword) {
    var resource = serverDatasourceDTO.getResourceByKeywordOrThrow(resourceKeyword);
    var uri = uriFactory.create(serverDatasourceDTO, resource);

    Optional<String> scope = Optional.empty();
    if (SCOPE_ASSET_LIST.equals(resourceKeyword)) {
      scope = resource.getKeywords().stream()
          .filter(keyword -> StringUtils.startsWith(keyword, LIST_ASSET_SCOPE + ":"))
          .map(keyword -> StringUtils.removeStart(keyword, LIST_ASSET_SCOPE + ":"))
          .findFirst();
    }
    var requestEntityVisitor = requestEntityVisitorFactory.create(uri, scope);
    return serverDatasourceDTO.getAuthMethod().accept(requestEntityVisitor);
  }

}
