package collaborate.api.datasource.metadata;

import collaborate.api.datasource.create.AuthenticationMetadataVisitor.Keys;
import collaborate.api.datasource.create.DatasourceDTOMetadataVisitor;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetadataService {

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

  public String getCertificate(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(Keys.CERTIFICATE_BASED_BASIC_AUTH_CA_EMAIL))
        .map(Metadata::getValue)
        .findFirst()
        .orElse("");
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
}
