package collaborate.api.datasource.businessdata.access;

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
        encodeBase64(key.getEncoded()) + CIPHER_SEPARATOR + encodeBase64(iv.getIV());
    var cipheredKeyAndIv = rsaCipherService.cipher(encodedKeyAndIv, encryptionKey);

    String cipheredToken = aesCipherService.cipher(toCipher, key, iv);
    return cipheredKeyAndIv + CIPHER_SEPARATOR + cipheredToken;
  }

  private byte[] decodeBase64(int keyIndex, String[] encodedKeyAndIv) {
    return Base64.getDecoder().decode(encodedKeyAndIv[keyIndex].getBytes());
  }

  private String encodeBase64(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  public String decipher(String message) {
    var keyAndToken = message.split(CIPHER_SEPARATOR);

    var encodedKeyAndIv = rsaCipherService.decipher(
        keyAndToken[KEY_AND_SPEC_INDEX],
        apiProperties.getPrivateEncryptionKey()
    ).split(CIPHER_SEPARATOR);

    SecretKey decipheredKey = getSecretKey(encodedKeyAndIv);
    IvParameterSpec ivSpec = getIvParameterSpec(encodedKeyAndIv);

    return aesCipherService.decipher(keyAndToken[TOKEN_INDEX], decipheredKey, ivSpec);
  }

  private IvParameterSpec getIvParameterSpec(String[] encodedKeyAndIv) {
    var byteIvResult = decodeBase64(IV_SPEC_INDEX, encodedKeyAndIv);
    return new IvParameterSpec(byteIvResult);
  }

  private SecretKey getSecretKey(String[] encodedKeyAndIv) {
    var byteKeyResult = decodeBase64(KEY_INDEX, encodedKeyAndIv);
    return new SecretKeySpec(byteKeyResult, 0, byteKeyResult.length, "AES");
  }


}
