package collaborate.api.datasource.gateway.traefik;

import static java.util.function.Function.identity;

import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TraefikProviderService {

  private final ObjectMapper objectMapper;
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

  private void save(TraefikProviderConfiguration providerConfiguration, String datasourceId) {
    traefikProviderDAO.save(providerConfiguration, datasourceId);
  }

  private void saveCertificate(DatasourceDTO datasource) {
    datasource.getAuthMethod().accept(saveCertificateVisitor);
  }

  public boolean exists(String datasourceId) {
    return traefikProviderDAO.exists(datasourceId);
  }

  /**
   * warning: Traefik won't handle this datasource directly (~1 seconds would be required)
   */
  public boolean save(Datasource datasource) {
    if (exists(datasource.getId())) {
      return false;
    }
    if (!datasource.getProvider().equals(TraefikProviderConfiguration.class.getName())) {
      throw new IllegalStateException(
          "Invalid datasource provider type:" + datasource.getProvider());
    }
    var traefikConfiguration = objectMapper.convertValue(
        datasource.getProviderConfiguration(),
        TraefikProviderConfiguration.class);
    save(traefikConfiguration, datasource.getId());
    return true;
  }

  public String buildDatasourceBaseUri(Datasource datasource) {
    return toTraefikProviderConfiguration(datasource)
        .map(conf -> conf.getHttp().findFirstServiceLoadBalancerUri())
        .flatMap(identity())
        .orElse("");
  }

  public Optional<TraefikProviderConfiguration> toTraefikProviderConfiguration(
      Datasource datasource) {
    var isTraefikProviderConfiguration = TraefikProviderConfiguration.class
        .getName()
        .equals(datasource.getProvider());
    if (!isTraefikProviderConfiguration) {
      return Optional.empty();
    } else {
      return Optional.of(
          objectMapper.convertValue(
              datasource.getProviderConfiguration(),
              TraefikProviderConfiguration.class
          )
      );
    }
  }
}
