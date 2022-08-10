package collaborate.api.security;

import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitRsaEncryptionKeyOnApplicationStartup {

  private final RsaEncryptionKeyService rsaEncryptionKeyService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    try {
      rsaEncryptionKeyService.ensureEncryptionKeyExists();
    } catch (NoSuchAlgorithmException e) {
      throw new EncryptionKeyInitException(e);
    }
  }
}
