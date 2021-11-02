package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TraefikProviderService {

  private final SaveCertificateVisitor saveCertificateVisitor;
  private final TraefikProviderDAO traefikProviderDAO;
  private final TraefikProviderConfigurationFactory traefikProviderConfigurationFactory;

  public TraefikProviderConfiguration save(DatasourceDTO datasource)
      throws DatasourceVisitorException, IOException {
    saveCertificate(datasource);

    var providerConfiguration = traefikProviderConfigurationFactory.create(datasource);
    traefikProviderDAO.save(providerConfiguration, datasource.getId().toString());
    return providerConfiguration;
  }

  private void saveCertificate(DatasourceDTO datasource) {
    datasource.getAuthMethod().accept(saveCertificateVisitor);
  }
}
