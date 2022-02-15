package collaborate.api.datasource.model.dto.web.authentication;

import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2ClientCredentialsGrant extends Authentication {

  private String grantType;
  @JsonProperty("authorizationServerUrl")
  private URI tokenEndpoint;
  private String clientId;
  @ToString.Exclude
  private String clientSecret;

  @Builder(toBuilder = true)
  public OAuth2ClientCredentialsGrant(
      PartnerTransferMethod partnerTransferMethod,
      String grantType,
      URI tokenEndpoint,
      String clientId,
      String clientSecret
  ) {
    super(null, partnerTransferMethod);
    this.grantType = grantType;
    this.tokenEndpoint = tokenEndpoint;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  public <T> T accept(AuthenticationVisitor<T> visitor) {
    return visitor.visitOAuth2(this);
  }

  @JsonIgnore
  public LinkedMultiValueMap<String, String> toEntityBody() {
    var entityBody = new LinkedMultiValueMap<String, String>();
    entityBody.add("grant_type", grantType);
    entityBody.add("client_id", clientId);
    entityBody.add("client_secret", clientSecret);
    return entityBody;
  }
}
