package collaborate.api.security;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CipherConfig {

  public CipherConfig() {
    Security.addProvider(new BouncyCastleProvider());
  }

}
