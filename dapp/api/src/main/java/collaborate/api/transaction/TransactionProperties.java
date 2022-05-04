package collaborate.api.transaction;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "transaction", ignoreUnknownFields = false)
public class TransactionProperties {

  private String tagClientUrl;
  private Boolean enabled;
  private List<TransactionWatcherProperty> watchers;
}
