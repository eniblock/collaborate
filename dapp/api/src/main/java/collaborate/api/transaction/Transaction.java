package collaborate.api.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
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
@Table(indexes = {
    @Index(columnList = "destination"),
    @Index(columnList = "source"),
    @Index(columnList = "entrypoint")
})
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
    var parameterValue = Optional.ofNullable(getParameters())
        .map(parameters -> parameters.get(key));

    return StringUtils.equals(
        expectedValue,
        parameterValue
            .filter(param -> !param.isNull())
            .map(JsonNode::asText)
            .orElse(null)
    );
  }

  public boolean isSender(String address) {
    return StringUtils.equals(source, address);
  }
}
