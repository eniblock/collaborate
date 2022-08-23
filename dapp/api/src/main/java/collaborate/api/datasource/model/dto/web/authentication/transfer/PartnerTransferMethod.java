package collaborate.api.datasource.model.dto.web.authentication.transfer;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CertificateBasedAuthorityEmail.class, name = CertificateBasedAuthorityEmail.TYPE_NAME),
    @JsonSubTypes.Type(value = OAuth2SharedCredentials.class, name = OAuth2SharedCredentials.TYPE_NAME),
})
@JsonInclude(Include.NON_NULL)
public abstract class PartnerTransferMethod implements Serializable {

  public abstract <T> T accept(TransferMethodVisitor<T> visitor);
}
