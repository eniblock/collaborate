package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.nft.find.TokenMetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TraefikProviderService {

  private final ObjectMapper objectMapper;
  private final SaveCertificateVisitor saveCertificateVisitor;
  private final TokenMetadataService tokenMetadataService;
  private final TraefikProperties traefikProperties;
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

  private void save(TraefikProviderConfiguration providerConfiguration, String datasourceId) {
    traefikProviderDAO.save(providerConfiguration, datasourceId);
  }

  private void saveCertificate(DatasourceDTO datasource) {
    datasource.getAuthMethod().accept(saveCertificateVisitor);
  }

  public boolean exists(String datasourceId) {
    return traefikProviderDAO.exists(datasourceId);
  }

  public void fetchByTokenId(Integer tokenId, String smartContractAddress) {
    tokenMetadataService.getDatasourceProviderConfigurations(
        tokenId,
        smartContractAddress
    ).forEach(this::storeDatasource);
  }

  private void storeDatasource(Datasource datasource) {
    if (!datasource.getProvider().equals(TraefikProviderConfiguration.class.getName())) {
      throw new IllegalStateException(
          "Invalid datasource provider type:" + datasource.getProvider());
    }
    var traefikConfiguration = objectMapper.convertValue(
        datasource.getProviderConfiguration(),
        TraefikProviderConfiguration.class);
    save(traefikConfiguration, datasource.getId());
  }
}
