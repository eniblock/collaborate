package collaborate.api.datasource;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.PURPOSE_TEST_CONNECTION;

import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.http.HttpURLConnectionVisitorFactory;
import collaborate.api.http.ResponseCodeOkPredicate;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TestConnectionVisitor implements DatasourceDTOVisitor<BooleanSupplier> {

  private final HttpURLConnectionVisitorFactory httpURLConnectionVisitorFactory;
  private final ResponseCodeOkPredicate responseCodeOkPredicate;
  private final URIFactory uriFactory;

  @Override
  public BooleanSupplier visitWebServerDatasource(WebServerDatasourceDTO webServerDatasourceDTO) {
    var resource = webServerDatasourceDTO.getResourceByKeywordOrThrow(PURPOSE_TEST_CONNECTION);
    var uri = uriFactory.create(webServerDatasourceDTO, resource);

    var httpURLConnectionVisitor = httpURLConnectionVisitorFactory.create(uri);
    var httpURLConnection = webServerDatasourceDTO.getAuthMethod().accept(httpURLConnectionVisitor);
    return () -> responseCodeOkPredicate.test(httpURLConnection);
  }

}
