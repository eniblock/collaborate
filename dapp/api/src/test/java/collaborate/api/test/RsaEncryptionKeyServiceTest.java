package collaborate.api.test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.security.RsaCipherService;
import collaborate.api.security.RsaEncryptionKeyService;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class RsaEncryptionKeyServiceTest {

  @Mock
  ApiProperties apiProperties;

  @Captor
  ArgumentCaptor<String> privateKeyCaptor;
  @Captor
  ArgumentCaptor<String> publicKeyCaptor;
  @InjectMocks
  RsaEncryptionKeyService rsaEncryptionKeyService;

  @ParameterizedTest
  @MethodSource("ensureEncryptionKeyExistsParams")
  void ensureEncryptionKeyExists_shouldDoNothing_withRsaPrivateKeyExists(String privateKey,
      int expectedInvocationNb)
      throws NoSuchAlgorithmException {
    // GIVEN
    when(apiProperties.getPrivateEncryptionKey()).thenReturn(privateKey);
    // WHEN
    rsaEncryptionKeyService.ensureEncryptionKeyExists();
    // THEN
    verify(apiProperties, times(expectedInvocationNb)).setPrivateEncryptionKey(anyString());
  }

  private static Stream<Arguments> ensureEncryptionKeyExistsParams() {
    return Stream.of(
        Arguments.of(null, 1),
        Arguments.of("  ", 1),
        Arguments.of("privateKey", 0)
    );
  }

  @Test
  void ensureEncryptionKeyExists_shouldGenerateUsableKeys()
      throws NoSuchAlgorithmException {
    // GIVEN
    // WHEN
    rsaEncryptionKeyService.ensureEncryptionKeyExists();

    // THEN
    verify(apiProperties).setPrivateEncryptionKey(privateKeyCaptor.capture());
    verify(apiProperties).setPublicEncryptionKey(publicKeyCaptor.capture());

    var cipherService = new RsaCipherService();
    String expectedSecret = "secret";
    var ciphered = cipherService.cipher(expectedSecret, publicKeyCaptor.getValue());
    var unciphered = cipherService.decipher(ciphered, privateKeyCaptor.getValue());
    assertThat(unciphered).isEqualTo(expectedSecret);

  }
}
