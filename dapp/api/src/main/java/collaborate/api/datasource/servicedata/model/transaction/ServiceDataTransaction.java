package collaborate.api.datasource.servicedata.model.transaction;

import collaborate.api.config.ISO8601JsonStringFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@IdClass(ServiceDataTransactionPK.class)
public class ServiceDataTransaction {

  @Id
  private String smartContract;

  @Id
  private String tokenId;

  private String entrypoint;

  private String assetId;

  private String ipfsMetadataURI;

  private String operator;

  @ISO8601JsonStringFormat
  private ZonedDateTime timestamp;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode parameters;
}
