package collaborate.api.datasource.businessdata;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import collaborate.api.datasource.create.RequestEntitySupplierFactory;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AssetListService {

  private final RequestEntitySupplierFactory requestEntitySupplierFactory;
  private final ObjectMapper objectMapper;

  public String getAssetListResponse(WebServerDatasourceDTO webServerDatasourceDTO) {
    var assetListResponseSupplier = requestEntitySupplierFactory.create(
        webServerDatasourceDTO,
        SCOPE_ASSET_LIST
    );

    log.debug("fetching assetList for datasource={}", webServerDatasourceDTO.getId());
    var response = assetListResponseSupplier.get();
    if (response.getStatusCode() != OK) {
      throw new ResponseStatusException(
          BAD_REQUEST,
          "Getting business data documents failed with responseCode=" + response.getStatusCode()
      );
    }
    try {
      return objectMapper.writeValueAsString(response.getBody());
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(
          BAD_REQUEST,
          "Getting business data documents failed reading response",
          e
      );
    }
  }
}
