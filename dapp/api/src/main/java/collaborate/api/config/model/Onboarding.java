package collaborate.api.config.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Onboarding {

  private String publicEncryptionKey;
  private String walletAddress;

}
