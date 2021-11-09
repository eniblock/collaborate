package collaborate.api.businessdata.access;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.OrganizationService;
import collaborate.api.security.AesCipherService;
import collaborate.api.security.RsaCipherService;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CipherJwtService {

  public static final int KEY_INDEX = 0;
  public static final int IV_SPEC_INDEX = 1;
  public static final int TOKEN_INDEX = 1;
  public static final int KEY_AND_SPEC_INDEX = 0;
  public static final String CIPHER_SEPARATOR = " ";
  private final ApiProperties apiProperties;
  private final OrganizationService organizationService;
  private final AesCipherService aesCipherService;
  private final RsaCipherService rsaCipherService;

  public String cipher(String toCipher, String requester) {
    var encryptionKey = organizationService.getByWalletAddress(requester).getEncryptionKey();

    var key = aesCipherService.generateKey(256);
    IvParameterSpec iv = aesCipherService.generateIv();
    var encodedKeyAndIv =
        base64Encode(key.getEncoded()) + CIPHER_SEPARATOR + base64Encode(iv.getIV());
    var cipheredKeyAndIv = rsaCipherService.cipher(encodedKeyAndIv, encryptionKey);
    // cipher message
    String cipheredToken = aesCipherService.cipher(toCipher, key, iv);
    return cipheredKeyAndIv + CIPHER_SEPARATOR + cipheredToken;
  }

  private String base64Encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  public String decipher(String message) {
    var keyAndToken = message.split(CIPHER_SEPARATOR);
    var encodedKeyAndIv = rsaCipherService.decipher(
        keyAndToken[KEY_AND_SPEC_INDEX],
        apiProperties.getPrivateKey()
    ).split(CIPHER_SEPARATOR);

    var byteKeyResult = Base64.getDecoder().decode(encodedKeyAndIv[KEY_INDEX].getBytes());
    SecretKey decipheredKey = new SecretKeySpec(byteKeyResult, 0, byteKeyResult.length, "AES");

    var byteIvResult = Base64.getDecoder().decode(encodedKeyAndIv[IV_SPEC_INDEX].getBytes());
    var ivResult = new IvParameterSpec(byteIvResult);

    return aesCipherService.decipher(keyAndToken[TOKEN_INDEX], decipheredKey, ivResult);
  }
}
