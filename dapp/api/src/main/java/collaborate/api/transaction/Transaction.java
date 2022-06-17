package collaborate.api.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
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
  @Id
  private String hash;
  private String block;
  private String type;
  private Long height;
  private String entrypoint;
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode parameters;

  public boolean isEntryPoint(String expectedEntrypoint) {
    return StringUtils.equals(expectedEntrypoint, this.entrypoint);
  }

  public boolean hasParameterValue(String key, String expectedValue) {
    var providerAddress = getParameters().get(key);
    return providerAddress != null && StringUtils.equals(expectedValue, providerAddress.asText());
  }
}
