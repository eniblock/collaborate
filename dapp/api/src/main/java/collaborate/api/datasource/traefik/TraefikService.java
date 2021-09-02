package collaborate.api.datasource.traefik;

import collaborate.api.datasource.domain.Datasource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TraefikService {

  private final DatasourceHttpProviderDAO datasourceHttpProviderDAO;
  private final DatasourceCertificateProviderDAO datasourceCertificateProviderDAO;

  public void create(Datasource datasource, String output) throws Exception {
    datasourceCertificateProviderDAO.save(datasource,output);
    datasourceHttpProviderDAO.save(datasource);
  }

}
