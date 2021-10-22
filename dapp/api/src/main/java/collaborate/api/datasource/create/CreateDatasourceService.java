package collaborate.api.datasource.create;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.create.provider.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.datasource.security.SaveAuthenticationVisitor;
import collaborate.api.datasource.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.traefik.routing.DatasourceKeySupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateDatasourceService {

  private final DatasourceDAO datasourceDAO;
  private final ObjectMapper objectMapper;
  private final ProviderMetadataFactory providerMetadataFactory;
  private final SaveAuthenticationVisitor saveAuthenticationVisitor;
  private final TraefikProviderService traefikProviderService;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;

  @Transactional
  public Datasource create(DatasourceDTO datasourceDTO) throws Exception {
    datasourceDTO.getAuthMethod().setDatasource(datasourceDTO);
    datasourceDTO.setId(uuidGenerator.randomUUID());

    datasourceDTO.getAuthMethod().accept(saveAuthenticationVisitor);

    var providerConfiguration = traefikProviderService.save(datasourceDTO);
    var datasource = buildDatasource(datasourceDTO, providerConfiguration);
    return datasourceDAO.save(datasource).getContent();
  }

  Datasource buildDatasource(
      DatasourceDTO datasourceDTO, TraefikProviderConfiguration providerConfiguration) {

    var authHeaderKeySupplier = new AuthHeaderKeySupplier(new DatasourceKeySupplier(datasourceDTO));
    providerConfiguration.getHttp().getMiddlewares().remove(authHeaderKeySupplier.get());

    return Datasource.builder()
        .id(datasourceDTO.getId().toString())
        .name(datasourceDTO.getName())
        .creationDatetime(ZonedDateTime.now(clock))
        .providerConfiguration(
            objectMapper.convertValue(providerConfiguration, LinkedHashMap.class))
        .provider(TraefikProviderConfiguration.class.getName())
        .providerMetadata(providerMetadataFactory.from(datasourceDTO))
        .build();
  }
}
