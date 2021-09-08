package collaborate.api.datasource;

import static collaborate.api.cache.CacheConfig.CacheNames.DATASOURCE;
import static collaborate.api.cache.CacheConfig.CacheNames.WEBSERVER_DATASOURCE;

import collaborate.api.datasource.create.CreateDatasourceDAO;
import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.repository.DataSourceRepository;
import collaborate.api.datasource.security.SaveAuthenticationToDatabaseVisitor;
import collaborate.api.datasource.traefik.TraefikService;
import collaborate.api.http.security.SSLContextException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

  private final CreateDatasourceDAO createDatasourceDAO;
  private final DataSourceRepository dataSourceRepository;
  private final TestConnectionFactory testConnectionFactory;
  private final ObjectMapper objectMapper;
  private final SaveAuthenticationToDatabaseVisitor saveAuthenticationToDatabaseVisitor;
  private final EntityManager entityManager;
  private final TraefikService traefikService;

  @Transactional
  public Datasource create(Datasource datasource,
      Optional<MultipartFile> pfxFile) throws Exception {
    Datasource datasourceResult = dataSourceRepository.saveAndFlush(datasource);
    entityManager.refresh(datasourceResult);
    datasourceResult.getAuthMethod().accept(saveAuthenticationToDatabaseVisitor);
    datasource.setId(datasourceResult.getId());
    traefikService.create(
        updatePfxFileContent(datasource, pfxFile),
        datasourceResult.getId().toString()
    );
    createDatasourceDAO.create(datasourceResult);
    return datasourceResult;
  }

  public boolean testConnection(Datasource datasource, Optional<MultipartFile> pfxFile)
      throws UnrecoverableKeyException, SSLContextException, IOException {
    BooleanSupplier connectionTester = testConnectionFactory.create(
        updatePfxFileContent(datasource, pfxFile));
    return connectionTester.getAsBoolean();
  }

  private Datasource updatePfxFileContent(Datasource datasource, Optional<MultipartFile> pfxFile)
      throws IOException {
    if (pfxFile.isPresent() && datasource.getAuthMethod() instanceof CertificateBasedBasicAuth) {
      datasource = objectMapper.readValue(
          objectMapper.writeValueAsString(datasource),
          Datasource.class
      );
      ((CertificateBasedBasicAuth) datasource.getAuthMethod())
          .setPfxFileContent(pfxFile.get().getBytes());
    }
    return datasource;
  }

  public Page<Datasource> search(Pageable pageable, String query) {
    return dataSourceRepository.findByNameIgnoreCaseLike(pageable, query);
  }

  @Cacheable(value = DATASOURCE)
  public Optional<Datasource> findById(UUID id) {
    return dataSourceRepository.findById(id);
  }

  @Cacheable(value = WEBSERVER_DATASOURCE)
  public WebServerDatasource getWebServerDatasourceByUUID(String datasourceUUID) {
    var datasourceOpt = findById(UUID.fromString(datasourceUUID));
    return datasourceOpt
        .filter(d -> d instanceof WebServerDatasource)
        .map(d -> (WebServerDatasource) d)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "No webserver datasource found for uuid=" + datasourceUUID)
        );
  }
}
