package collaborate.api.security;

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.cache.CacheService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.user.metadata.UserMetadataService;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RsaEncryptionKeyService {

  public static final String ENCRYPTION_VAULT_KEY = "encryptionKey";
  private final ApiProperties apiProperties;
  private final CacheService cacheService;

  private final UserMetadataService userMetadataService;

  public void ensureEncryptionKeyExists() throws NoSuchAlgorithmException {
    var encryptionKey = userMetadataService.find("encryptionKey", EncryptionKey.class);
    if (encryptionKey.isPresent()) {
      log.info("Using the key provided by Vault");
      apiProperties.setPrivateKey(encryptionKey.get().getPrivateKey());
      apiProperties.setPublicEncryptionKey(encryptionKey.get().getPublicKey());
    } else {
      if (StringUtils.isBlank(apiProperties.getPrivateKey())) {
        log.info("Generation a new keys");
        KeyPair keyPair = generateRSAKeyPair();
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        apiProperties.setPrivateKey(privateKey);
        apiProperties.setPublicEncryptionKey(publicKey);
        cacheService.clearOrThrow(CacheNames.ORGANIZATION);
      }
      log.info("Persisting key in Vault");
      userMetadataService.upsertMetadata(
          ENCRYPTION_VAULT_KEY,
          EncryptionKey.builder()
              .privateKey(apiProperties.getPrivateKey())
              .publicKey(apiProperties.getPublicEncryptionKey())
              .build()
      );
    }
  }

  private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);

    return generator.generateKeyPair();
  }
}
