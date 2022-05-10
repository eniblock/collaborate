package collaborate.api.datasource;

import static collaborate.api.datasource.create.AuthenticationMetadataVisitor.Keys.PARTNER_TRANSFER_METHOD;

import collaborate.api.datasource.create.AuthenticationMetadataVisitor.Keys;
import collaborate.api.datasource.create.DatasourceDTOMetadataVisitor;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceMetadataService {

  public static final String LIST_ASSET_SCOPE = "list-asset-scope";

  private final ObjectMapper objectMapper;
  private final TypeReference<List<String>> listOfString = new TypeReference<>() {
  };

  public String getType(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(DatasourceDTOMetadataVisitor.Keys.DATASOURCE_TYPE))
        .map(Metadata::getValue)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No datasource type found for datasource=" + datasource.getId())
        );
  }

  public String getAuthentication(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(Keys.DATASOURCE_AUTHENTICATION))
        .map(Metadata::getValue)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No authentication type found for datasource=" + datasource.getId())
        );
  }

  public List<String> getPurpose(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(DatasourceDTOMetadataVisitor.Keys.DATASOURCE_PURPOSE))
        .map(Metadata::getValue)
        .findFirst()
        .map(purpose -> {
          try {
            return objectMapper.readValue(purpose, listOfString);
          } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can't read datasource purpose from " + purpose);
          }
        })
        .orElseThrow(() -> new IllegalStateException(
            "No purpose found for datasource=" + datasource.getId())
        );
  }

  public PartnerTransferMethod getPartnerTransferMethod(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(PARTNER_TRANSFER_METHOD))
        .map(Metadata::getValue)
        .findFirst()
        .map(transferMethod -> {
              try {
                return objectMapper.readValue(transferMethod, PartnerTransferMethod.class);
              } catch (JsonProcessingException e) {
                throw new IllegalStateException("Can't read datasource purpose from " + transferMethod);
              }
            }
        )
        .orElseThrow(() -> new IllegalStateException(
            "No transferMethod found for datasource=" + datasource.getId())
        );
  }

  public Optional<String> getAssetListScope(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(LIST_ASSET_SCOPE))
        .map(Metadata::getValue)
        .findFirst();
  }

  public Map<String, String> findByAlias(Datasource datasource, String resourceAlias) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> StringUtils.startsWith(a.getName(), resourceAlias))
        .collect(Collectors.toMap(
            m -> StringUtils.removeStart(m.getName(), resourceAlias + ":"),
            Metadata::getValue)
        );
  }
}
