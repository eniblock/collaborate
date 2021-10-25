package collaborate.api.datasource.metadata;

import collaborate.api.datasource.create.AuthenticationMetadataVisitor.Keys;
import collaborate.api.datasource.create.DatasourceDTOMetadataVisitor;
import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.Datasource;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

  public String buildDatasourceType(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(DatasourceDTOMetadataVisitor.Keys.DATASOURCE_TYPE))
        .map(Attribute::getValue)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No datasource type found for datasource=" + datasource.getId())
        );
  }

  public String buildAuthentication(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(Keys.DATASOURCE_AUTHENTICATION))
        .map(Attribute::getValue)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No authentication type found for datasource=" + datasource.getId())
        );
  }

  public String buildCertificate(Datasource datasource) {
    return datasource.getProviderMetadata().stream()
        .filter(a -> a.getName().equals(Keys.CERTIFICATE_BASED_BASIC_AUTH_CA_EMAIL))
        .map(Attribute::getValue)
        .findFirst()
        .orElse("");
  }

}
