package collaborate.api.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CipherService {
    private final Cipher cipher;

    public static PublicKey getKey(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key);

            X509EncodedKeySpec x509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(x509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The encryption process use the public key to encrypt the string.
     * It relies on the encrypt mode ECIES which permits to use ESCDA keys.
     * The algorithm consists in 3 steps:
     * - transform the string into an array of bytes using UTF-8
     * - cipher the bytes with the public key
     * - encode the cyphered bytes into a base64 string
     */
    synchronized public String cipher(String deciphered, PublicKey publicKey) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipher.update(deciphered.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipher.doFinal());
    }

    /**
     * Decrypt a ciphered string with the private key of the public key used to encrypt.
     * The algorithm consists in 3 steps:
     * - decode the string with base64 into an array of bytes
     * - decipher the bytes with the private key
     * - transform the bytes into a string using UTF-8
     *
     */
    public String decipher(String ciphered, PrivateKey privateKey) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(ciphered)), StandardCharsets.UTF_8);
    }
}
