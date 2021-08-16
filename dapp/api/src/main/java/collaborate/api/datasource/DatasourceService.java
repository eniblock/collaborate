package collaborate.api.datasource;

import collaborate.api.datasource.domain.BasicAuthDto;
import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.DatasourceClientSecret;
import collaborate.api.datasource.domain.authentication.BasicAuth;
import collaborate.api.datasource.domain.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.authentication.Oauth;
import collaborate.api.datasource.repository.DataSourceRepository;
import collaborate.api.http.security.SSLContextException;
import collaborate.api.security.VaultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.function.BooleanSupplier;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

  private final DataSourceRepository dataSourceRepository;
  private final DatasourceTestConnectionFactory datasourceTestConnectionFactory;
  private final ObjectMapper objectMapper;
  private final ModelMapper modelMapper;
  private final VaultService vaultService;

  @Transactional
  public DataSource create(DataSource datasource) {
    DataSource datasourceResult = dataSourceRepository.save(datasource);

    if (datasource.getAuthMethod() instanceof BasicAuth) {
      saveBasicAuthCredentials(datasource);
    }

    if (datasource.getAuthMethod() instanceof Oauth) {
      saveOAuthCredentials(datasource);
    }

    return datasourceResult;
  }

  public Page<DataSource> search(Pageable pageable, String query) {
    return dataSourceRepository.findByNameIgnoreCaseLike(pageable, query);
  }

  public DataSource findById(Long id) {
    return dataSourceRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  void saveBasicAuthCredentials(DataSource datasource) {
    BasicAuthDto datasourceBasicAuthDto = modelMapper
        .map(datasource.getAuthMethod(), BasicAuthDto.class);
    vaultService.put("datasource/" + datasource.getId() + "/authentication",
        datasourceBasicAuthDto);
  }

  void saveOAuthCredentials(DataSource datasource) {
    Oauth authentication = (Oauth) datasource.getAuthMethod();

    DatasourceClientSecret datasourceClientSecret = DatasourceClientSecret.builder()
        .clientId(authentication.getClientId())
        .clientSecret(authentication.getClientSecret())
        .build();

    vaultService.put("datasources/" + datasource.getId(), datasourceClientSecret);
  }

  public boolean testBasicAuthConnection(DataSource datasource, byte[] pfxFileContent)
      throws UnrecoverableKeyException, SSLContextException, IOException {
    if (pfxFileContent != null && datasource.getAuthMethod() instanceof CertificateBasedBasicAuth) {
      datasource = objectMapper.readValue(
          objectMapper.writeValueAsString(datasource),
          DataSource.class
      );
      ((CertificateBasedBasicAuth) datasource.getAuthMethod()).setPfxFileContent(pfxFileContent);
    }

    BooleanSupplier connectionTester = datasourceTestConnectionFactory.create(datasource);

    return connectionTester.getAsBoolean();
  }

}
