package collaborate.api.datasource.model.dto.web.authentication;

import java.net.URI;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2 extends Authentication {

  private String accessMethod;
  private URI issuerIdentifierUri;
  private URI wellKnownURIPathSuffix;
  private String transferMethod;
  private String clientId;
  @ToString.Exclude
  @Transient
  private String clientSecret;

  @Builder(toBuilder = true)
  public OAuth2(
      String accessMethod,
      URI issuerIdentifierUri,
      URI wellKnownURIPathSuffix,
      String transferMethod,
      String clientId,
      String clientSecret
  ) {
    this.accessMethod = accessMethod;
    this.issuerIdentifierUri = issuerIdentifierUri;
    this.wellKnownURIPathSuffix = wellKnownURIPathSuffix;
    this.transferMethod = transferMethod;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  public <T> T accept(AuthenticationVisitor<T> visitor) {
    return visitor.visitOAuth2(this);
  }
}