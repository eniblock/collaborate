package collaborate.api.transaction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@Validated
@ConfigurationProperties(prefix = "transaction-watchers", ignoreUnknownFields = false)
public class TransactionWatchersProperties {

  private String tagClientUrl;
  private Boolean enabled;
  @NotNull
  @Positive
  private Long fixedDelayInMs;
}
