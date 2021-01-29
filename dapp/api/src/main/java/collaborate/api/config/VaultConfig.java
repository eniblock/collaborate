package collaborate.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultOperations;

@Configuration
public class VaultConfig {
    @Autowired
    VaultOperations vaultOperations;

    @Bean
    public VaultKeyValueOperations keyValue(RestTemplateBuilder builder) {
        return vaultOperations.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
    }
}
