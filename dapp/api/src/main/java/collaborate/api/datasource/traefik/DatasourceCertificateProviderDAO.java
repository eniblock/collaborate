package collaborate.api.datasource.traefik;

import collaborate.api.datasource.domain.Datasource;
import collaborate.api.security.PfxUnProtector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DatasourceCertificateProviderDAO {

  private final PfxUnProtector pfxUnProtector;

  public void save(Datasource datasource, String output) throws Exception {
    var saveCertificateVisitor = new SaveCertificateVisitor(pfxUnProtector, output);
    datasource.getAuthMethod().accept(saveCertificateVisitor);
  }
}
