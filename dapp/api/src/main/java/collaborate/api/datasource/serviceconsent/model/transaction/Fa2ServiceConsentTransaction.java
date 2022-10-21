package collaborate.api.datasource.serviceconsent.model.transaction;

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
@IdClass(Fa2ServiceConsentTransactionPK.class)
public class Fa2ServiceConsentTransaction {

  @Id
  private String smartContract;

  @Id
  private Long tokenId;

  private String entrypoint;

  private String owner;

  private String ipfsMetadata;

  private Long passportId;

  @ISO8601JsonStringFormat
  private ZonedDateTime timestamp;


  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode parameters;
}
