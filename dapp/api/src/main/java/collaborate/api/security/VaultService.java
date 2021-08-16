package collaborate.api.security;

import collaborate.api.datasource.domain.DatasourceClientSecret;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultService {

  public void saveSecret(String key, Object value){
    throw new NotImplementedException();
  }

  public void saveClientSecret(String datasourceIdentifier, DatasourceClientSecret clientSecret){
    throw new NotImplementedException();
  }
}
