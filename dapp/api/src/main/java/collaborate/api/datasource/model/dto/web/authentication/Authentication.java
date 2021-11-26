package collaborate.api.datasource.model.dto.web.authentication;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BasicAuth.class, name = "BasicAuth"),
    @JsonSubTypes.Type(value = CertificateBasedBasicAuth.class, name = "CertificateBasedBasicAuth"),
    @JsonSubTypes.Type(value = OAuth2.class, name = "OAuth2")
})
@JsonInclude(Include.NON_NULL)
@Data
@EqualsAndHashCode()
public abstract class Authentication implements Serializable {

  @JsonIgnore
  private DatasourceDTO datasource;

  public abstract <T> T accept(AuthenticationVisitor<T> visitor);
}
