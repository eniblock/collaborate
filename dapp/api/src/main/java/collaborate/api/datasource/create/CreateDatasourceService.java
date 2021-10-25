package collaborate.api.datasource.create;

import static java.util.stream.Collectors.toSet;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.create.provider.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.datasource.security.SaveAuthenticationVisitor;
import collaborate.api.datasource.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.traefik.routing.DatasourceKeySupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateDatasourceService {

  private final AuthenticationMetadataVisitor authenticationMetadataVisitor;
  private final DatasourceDAO datasourceDAO;
  private final DatasourceDTOMetadataVisitor datasourceDTOMetadataVisitor;
  private final ObjectMapper objectMapper;
  private final SaveAuthenticationVisitor saveAuthenticationVisitor;
  private final TraefikProviderService traefikProviderService;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;
  private final CreateBusinessDataNftDAO createBusinessDataNftDAO;

  @Transactional
  public Datasource create(DatasourceDTO datasourceDTO)
      throws DatasourceVisitorException, IOException {
    datasourceDTO.getAuthMethod().setDatasource(datasourceDTO);
    datasourceDTO.setId(uuidGenerator.randomUUID());

    datasourceDTO.getAuthMethod().accept(saveAuthenticationVisitor);

    var providerConfiguration = traefikProviderService.save(datasourceDTO);
    var datasource = buildDatasource(datasourceDTO, providerConfiguration);
    if (true) { // TODO check if the datasource is a datasource for business data
      // Datasource for business data
      var ipfsMetadataUri = saveMetadataInIPFS(datasourceDTO);
      createBusinessDataNftDAO.mintBusinessDataNFT(datasourceDTO.getId(), ipfsMetadataUri);
    }
    return datasourceDAO.save(datasource).getContent();
  }

  String saveMetadataInIPFS(DatasourceDTO datasourceDTO) {
    // TODO !!!
    return "TODO_ipfs_metadata";
  }

  Datasource buildDatasource(
      DatasourceDTO datasourceDTO,
      TraefikProviderConfiguration providerConfiguration
  ) throws DatasourceVisitorException {

    var authHeaderKeySupplier = new AuthHeaderKeySupplier(new DatasourceKeySupplier(datasourceDTO));
    providerConfiguration.getHttp().getMiddlewares().remove(authHeaderKeySupplier.get());

    return Datasource.builder()
        .id(datasourceDTO.getId().toString())
        .name(datasourceDTO.getName())
        .creationDatetime(ZonedDateTime.now(clock))
        .providerConfiguration(
            objectMapper.convertValue(providerConfiguration, LinkedHashMap.class)
        ).provider(TraefikProviderConfiguration.class.getName())
        .providerMetadata(buildMetadata(datasourceDTO))
        .build();
  }

  Set<Attribute> buildMetadata(DatasourceDTO datasourceDTO) throws DatasourceVisitorException {
    return Stream.concat(
        datasourceDTO.getAuthMethod().accept(authenticationMetadataVisitor),
        datasourceDTO.accept(datasourceDTOMetadataVisitor)
    ).collect(toSet());
  }
}
