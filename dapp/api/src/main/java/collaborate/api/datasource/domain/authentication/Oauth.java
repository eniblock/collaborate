package collaborate.api.datasource.domain.authentication;

import java.net.URI;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class Oauth extends Authentication{

  private String accessMethod;

  private URI issuerIdentifierUri;

  private URI wellKnownURIPathSuffix;

  private String transferMethod;

  private String clientId;
  @ToString.Exclude
  @Transient
  private String clientSecret;

  @Builder(toBuilder = true)
  public Oauth(
      String accessMethod,
      URI issuerIdentifierUri,
      URI wellKnownURIPathSuffix,
      String transferMethod,
      String clientId,
      String clientSecret
  ){
    this.accessMethod = accessMethod;
    this.issuerIdentifierUri = issuerIdentifierUri;
    this.wellKnownURIPathSuffix = wellKnownURIPathSuffix;
    this.transferMethod = transferMethod;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }
}
