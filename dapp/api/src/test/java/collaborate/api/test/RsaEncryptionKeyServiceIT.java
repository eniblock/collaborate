package collaborate.api.test;


import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.cache.CacheConfig;
import collaborate.api.cache.CacheService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.security.CipherConfig;
import collaborate.api.security.RsaCipherService;
import collaborate.api.security.RsaEncryptionKeyService;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import collaborate.api.user.metadata.UserMetadataService;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        CacheConfig.class,
        CacheService.class,
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        ApiProperties.class,
        CipherConfig.class,
        RsaCipherService.class,
        RsaEncryptionKeyService.class})
class RsaEncryptionKeyServiceIT {

  @Autowired
  RsaEncryptionKeyService rsaEncryptionKeyService;
  @MockBean
  UserMetadataService userMetadataService;
  @Autowired
  ApiProperties apiProperties;

  @Test
  void ensureEncryptionKeyExists_shouldGenerateUsableKeys()
      throws NoSuchAlgorithmException {
    // GIVEN
    apiProperties.setPublicEncryptionKey(null);
    apiProperties.setPrivateKey(null);
    // WHEN
    rsaEncryptionKeyService.ensureEncryptionKeyExists();

    // THEN
    var cipherService = new RsaCipherService();
    String expectedSecret = "secret";
    var ciphered = cipherService.cipher(expectedSecret, apiProperties.getPublicEncryptionKey());
    var unciphered = cipherService.decipher(ciphered, apiProperties.getPrivateKey());
    assertThat(unciphered).isEqualTo(expectedSecret);
  }
}
