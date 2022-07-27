package collaborate.api.security;

import collaborate.api.config.api.ApiProperties;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RsaEncryptionKeyService {

  private final ApiProperties apiProperties;

  public void ensureEncryptionKeyExists() throws NoSuchAlgorithmException {
    boolean hasPrivateKey = StringUtils.isNotBlank(apiProperties.getPrivateEncryptionKey());
    if (!hasPrivateKey) {
      log.info("No encryption key found for the current organization, creating one");
      KeyPair keyPair = generateRSAKeyPair();
      String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
      String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

      apiProperties.setPrivateEncryptionKey(privateKey);
      apiProperties.setPublicEncryptionKey(publicKey);
    }

  }

  private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);

    return generator.generateKeyPair();
  }
}
