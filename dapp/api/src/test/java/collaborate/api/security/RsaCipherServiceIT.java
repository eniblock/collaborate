package collaborate.api.security;

import static collaborate.api.security.RsaFeatures.PRIVATE_KEY;
import static collaborate.api.security.RsaFeatures.PUBLIC_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * RAS Keys can be generated with the following commands<br> NB: It won't be possible to cipher a
 * message longer than the key length
 * <ul>
 *   <li><code>openssl genrsa -out keypair.pem 1024</code></li>
 *   <li>PKCS8 public key:<code>openssl rsa -in keypair.pem -pubout -out publickey.crt</code></li>
 *   <li>PKCS8 private key<code>openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out pkcs8.key</code></li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        CipherConfig.class,
        RsaCipherService.class})
class RsaCipherServiceIT {

  @Autowired
  RsaCipherService rsaCipherService;

  @Test
  void cipherThenUncipher_shouldBeIdentity() {

    var secret = "iGTCCJ/6kFconAZAms5jcHEosfswtSE0NPo09KPhqgw= qTbhFpHCklgs1fAYTZ2Uhw==";
    // WHEN
    var ciphered = rsaCipherService.cipher(secret, PUBLIC_KEY);
    var deciphered = rsaCipherService.decipher(ciphered, PRIVATE_KEY);
    // THEN
    assertThat(deciphered).isEqualTo(secret);
  }
}
