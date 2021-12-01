package collaborate.api.datasource.model.dto.web.authentication;

import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import java.net.URI;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2ClientCredentialsGrant extends Authentication {

  private String grantType;
  private URI issuerIdentifierUri;
  private URI wellKnownURIPathSuffix;
  private String clientId;
  @ToString.Exclude
  private String clientSecret;

  @Builder(toBuilder = true)
  public OAuth2ClientCredentialsGrant(
      PartnerTransferMethod partnerTransferMethod,
      String grantType,
      URI issuerIdentifierUri,
      URI wellKnownURIPathSuffix,
      String clientId,
      String clientSecret
  ) {
    super(null, partnerTransferMethod);
    this.grantType = grantType;
    this.issuerIdentifierUri = issuerIdentifierUri;
    this.wellKnownURIPathSuffix = wellKnownURIPathSuffix;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  public <T> T accept(AuthenticationVisitor<T> visitor) {
    return visitor.visitOAuth2(this);
  }
}
