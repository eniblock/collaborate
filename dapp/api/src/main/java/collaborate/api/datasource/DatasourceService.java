package collaborate.api.datasource;

import collaborate.api.datasource.create.CreateDatasourceDAO;
import collaborate.api.datasource.domain.Datasource;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  public Optional<Datasource> findById(UUID id) {
    return dataSourceRepository.findById(id);
  }
}
