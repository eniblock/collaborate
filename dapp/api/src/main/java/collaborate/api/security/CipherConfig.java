package collaborate.api.security;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CipherConfig {

  private static final String CIPHER_INSTANCE = "RSA/None/OAEPWITHSHA-256ANDMGF1PADDING";

  @Bean
  public Cipher cipherFactory() throws NoSuchPaddingException, NoSuchAlgorithmException {
    Security.addProvider(new BouncyCastleProvider());
    return Cipher.getInstance(CipherConfig.CIPHER_INSTANCE);
  }
}
