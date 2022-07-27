package collaborate.api.test;


import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.security.CipherConfig;
import collaborate.api.security.RsaCipherService;
import collaborate.api.security.RsaEncryptionKeyService;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        ApiProperties.class,
        CipherConfig.class,
        RsaCipherService.class,
        RsaEncryptionKeyService.class})
class RsaEncryptionKeyServiceIT {

  @Autowired
  RsaCipherService rsaCipherService;
  @Autowired
  RsaEncryptionKeyService rsaEncryptionKeyService;
  @Autowired
  ApiProperties apiProperties;

  @Test
  void ensureEncryptionKeyExists_shouldGenerateUsableKeys()
      throws NoSuchAlgorithmException {
    // GIVEN
    apiProperties.setPublicEncryptionKey(null);
    apiProperties.setPrivateEncryptionKey(null);
    // WHEN
    rsaEncryptionKeyService.ensureEncryptionKeyExists();

    // THEN
    var cipherService = new RsaCipherService();
    String expectedSecret = "secret";
    var ciphered = cipherService.cipher(expectedSecret, apiProperties.getPublicEncryptionKey());
    var unciphered = cipherService.decipher(ciphered, apiProperties.getPrivateEncryptionKey());
    assertThat(unciphered).isEqualTo(expectedSecret);
  }
}
