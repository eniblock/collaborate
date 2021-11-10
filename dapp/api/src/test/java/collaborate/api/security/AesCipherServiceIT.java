package collaborate.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * To generate Diffie Hellman: https://sandilands.info/sgordon/diffie-hellman-secret-key-exchange-with-openssl
 * <p>
 * <p>
 * <ul>
 *   <li>Generate DH Parameters and store it in the SC ?:<code>openssl genpkey -genparam -algorithm DH -out dhp.pem</code></li>
 *   <li>Use the public DH parameters to generate their own private and public key:<code>openssl genpkey -paramfile dhp.pem -out dhkey1.pem</code></li>
 *   <li>First extract the public key (to store SC):<code>openssl pkey -in dhkey1.pem -pubout -out dhpub1.pem</code></li>
 *   <li>Derive the shared secret: (from code)<code>openssl pkeyutl -derive -inkey dhkey1.pem -peerkey dhpub2.pem -out secret1.bin</code></li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        CipherConfig.class,
        AesCipherService.class,
        RsaCipherService.class
    })
class AesCipherServiceIT {

  @Autowired
  AesCipherService aesCipherService;

  @Autowired
  RsaCipherService rsaCipherService;

  @Test
  void cipherThenUncipher_shouldBeIdentity() {
    // GIVEN
    String toCipher = "small-secret";
    SecretKey secretKey = aesCipherService.generateKey(128);
    IvParameterSpec ivParameterSpec = aesCipherService.generateIv();
    // WHEN
    String aesCipheredText = aesCipherService.cipher(toCipher, secretKey, ivParameterSpec);
    String decipheredResult = aesCipherService.decipher(aesCipheredText, secretKey,
        ivParameterSpec);
    // THEN
    assertThat(decipheredResult).isEqualTo(toCipher);
  }
}
