package collaborate.api.datasource.model.dto.web.authentication;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BasicAuth.class, name = "BasicAuth"),
    @JsonSubTypes.Type(value = CertificateBasedBasicAuth.class, name = "CertificateBasedBasicAuth"),
    @JsonSubTypes.Type(value = OAuth2ClientCredentialsGrant.class, name = "OAuth2ClientCredentialsGrant")
})
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
public abstract class Authentication implements Serializable {

  @JsonIgnore
  private DatasourceDTO datasource;

  protected PartnerTransferMethod partnerTransferMethod;

  public abstract <T> T accept(AuthenticationVisitor<T> visitor);
}
