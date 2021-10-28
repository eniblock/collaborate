package collaborate.api.businessdata.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class TransactionEntity {

  @Id
  @GeneratedValue
  private Long id;
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
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode parameters;

}
