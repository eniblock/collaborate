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
    @JsonSubTypes.Type(value = CertificateBasedAuthorityEmail.class, name = "CertificateBasedAuthorityEmail"),
    @JsonSubTypes.Type(value = OAuth2RefreshToken.class, name = "OAuth2RefreshToken"),
})
@JsonInclude(Include.NON_NULL)
public interface PartnerTransferMethod extends Serializable {

}
