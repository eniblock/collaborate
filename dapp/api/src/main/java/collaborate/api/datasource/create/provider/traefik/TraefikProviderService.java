package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TraefikProviderService {

  private final SaveCertificateVisitor saveCertificateVisitor;
  private final TraefikProviderDAO traefikProviderDAO;
  private final TraefikProviderConfigurationFactory traefikProviderConfigurationFactory;

  public TraefikProviderConfiguration save(DatasourceDTO datasource)
      throws DatasourceVisitorException {
    saveCertificate(datasource);

    var providerConfiguration = traefikProviderConfigurationFactory.create(datasource);
    var datasourceId = datasource.getId().toString();
    save(providerConfiguration, datasourceId);
    return providerConfiguration;
  }

  public void save(TraefikProviderConfiguration providerConfiguration, String datasourceId) {
    traefikProviderDAO.save(providerConfiguration, datasourceId);
  }

  private void saveCertificate(DatasourceDTO datasource) {
    datasource.getAuthMethod().accept(saveCertificateVisitor);
  }
}
