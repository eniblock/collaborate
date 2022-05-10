package collaborate.api.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Transaction {

  private String indexer;
  private String destination;
  private String source;
  private ZonedDateTime timestamp;
  private String status;
  private Double bakerFee;
  private Double storageFee;
  private Long storageLimit;
  private Long counter;
  private String hash;
  private String block;
  private String type;
  private Long height;
  private String entrypoint;
  private JsonNode parameters;

  public boolean isEntryPoint(String expectedEntrypoint) {
    return StringUtils.equals(expectedEntrypoint, this.entrypoint);
  }

  public boolean hasParameterValue(String key, String expectedValue) {
    var providerAddress = getParameters().get(key);
    return providerAddress != null && StringUtils.equals(expectedValue, providerAddress.asText());
  }
}
