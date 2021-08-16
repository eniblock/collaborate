package collaborate.api.datasource.domain.authentication;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

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
import javax.persistence.Table;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BasicAuth.class, name = "BasicAuth"),
    @JsonSubTypes.Type(value = CertificateBasedBasicAuth.class, name = "CertificateBasedBasicAuth"),
    @JsonSubTypes.Type(value = Oauth.class, name = "OAuth")
})

@Table(name="authentication")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(Include.NON_NULL)
@Entity
public abstract class Authentication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
