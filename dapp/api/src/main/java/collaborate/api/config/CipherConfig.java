package collaborate.api.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import java.security.Security;

@Configuration
public class CipherConfig {
    private final static String CIPHER_INSTANCE = "RSA";

    @Bean
    public Cipher cipherFactory() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        return Cipher.getInstance(CIPHER_INSTANCE);
    }
}
