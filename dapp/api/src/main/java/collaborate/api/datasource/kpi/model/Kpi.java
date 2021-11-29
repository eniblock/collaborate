package collaborate.api.datasource.kpi.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = @Index(columnList = "kpiKey"))
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Kpi {

  @Id
  @GeneratedValue
  private Long id;

  @ISO8601JsonStringFormat
  private ZonedDateTime createdAt;

  private String kpiKey;

  private String organizationWallet;

  
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode parameters;

  public Optional<Object> findParameter(String key) {
    if (parameters != null) {
      return Optional.ofNullable(parameters.get(key));
    }
    return Optional.empty();
  }
}
