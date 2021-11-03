package collaborate.api.transaction;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "transaction")
public class TransactionProperties {

  private List<TransactionWatcherProperty> watchers;
}
