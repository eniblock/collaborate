package collaborate.api.datasource.domain.web.authentication;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.datasource.domain.Datasource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BasicAuth.class, name = "BasicAuth"),
    @JsonSubTypes.Type(value = CertificateBasedBasicAuth.class, name = "CertificateBasedBasicAuth"),
    @JsonSubTypes.Type(value = OAuth2.class, name = "OAuth2")
})
@Table(name = "authentication")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(Include.NON_NULL)
@Entity
@Data
@EqualsAndHashCode()
public abstract class Authentication implements Serializable {

  @JsonIgnore
  @OneToOne(mappedBy = "authMethod")
  private Datasource datasource;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  public abstract void accept(AuthenticationVisitor visitor);
}