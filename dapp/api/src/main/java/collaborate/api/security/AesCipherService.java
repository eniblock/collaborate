package collaborate.api.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AesCipherService {

  public static final String AES_ALGORITHM = "AES/GCM/NoPadding";

  public SecretKey generateKey(int n) {
    try {
      var keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(n);
      return keyGenerator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public IvParameterSpec generateIv() {
    byte[] iv = new byte[16];
    new SecureRandom().nextBytes(iv);
    return new IvParameterSpec(iv);
  }

  public String cipher(String input, SecretKey key, IvParameterSpec iv) {
    try {
      var cipher = Cipher.getInstance(AES_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);
      byte[] cipherText = cipher.doFinal(input.getBytes());
      return Base64.getEncoder().encodeToString(cipherText);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public String decipher(String cipherText, SecretKey key, IvParameterSpec iv) {
    try {
      Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key, iv);
      byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
      return new String(plainText);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
