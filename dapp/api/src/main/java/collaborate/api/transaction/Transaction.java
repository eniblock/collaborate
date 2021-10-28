package collaborate.api.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

}
