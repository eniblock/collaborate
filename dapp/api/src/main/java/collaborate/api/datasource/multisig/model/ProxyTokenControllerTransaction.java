package collaborate.api.datasource.multisig.model;

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
@IdClass(ProxyTokenControllerTransactionPK.class)
public class ProxyTokenControllerTransaction {

  @Id
  private String smartContract;

  @Id
  private Long multiSigId;

  private String owner;

  private String operator;

  private ZonedDateTime timestamp;

  private String metadata;

  private String entrypoint;

  private Boolean isSigned;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode parameters;
}
