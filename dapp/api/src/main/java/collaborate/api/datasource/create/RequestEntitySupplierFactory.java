package collaborate.api.datasource.create;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.SCOPE_PREFIX;

import collaborate.api.datasource.URIFactory;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.http.RequestEntityVisitorFactory;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
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

    Optional<String> scope = resource.findFirstKeywordRemovingPrefix(SCOPE_PREFIX);
    var requestEntityVisitor = requestEntityVisitorFactory.create(uri, scope);
    return serverDatasourceDTO.getAuthMethod().accept(requestEntityVisitor);
  }

}
