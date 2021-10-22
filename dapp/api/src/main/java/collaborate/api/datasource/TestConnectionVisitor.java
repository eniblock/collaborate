package collaborate.api.datasource;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;

import collaborate.api.datasource.create.HttpURLConnectionFactory;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.http.ResponseCodeOkPredicate;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TestConnectionVisitor implements DatasourceDTOVisitor<BooleanSupplier> {

  private final HttpURLConnectionFactory httpUrlConnectionFactory;
  private final ResponseCodeOkPredicate responseCodeOkPredicate;

  @Override
  public BooleanSupplier visitWebServerDatasource(WebServerDatasourceDTO serverDatasourceDTO) {
    var httpURLConnection = httpUrlConnectionFactory.create(serverDatasourceDTO, SCOPE_ASSET_LIST);
    return () -> responseCodeOkPredicate.test(httpURLConnection);
  }

}
