package collaborate.api.datasource.create;

import static collaborate.api.datasource.create.AuthenticationMetadataVisitor.Keys.CERTIFICATE_BASED_BASIC_AUTH_CA_EMAIL;
import static lombok.AccessLevel.PRIVATE;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMetadataVisitor implements AuthenticationVisitor<Stream<Metadata>> {

  @NoArgsConstructor(access = PRIVATE)
  public static final class Keys {

    public static final String CERTIFICATE_BASED_BASIC_AUTH_CA_EMAIL = "datasource:caEmail";
    public static final String DATASOURCE_AUTHENTICATION = "datasource:authentication";
  }

  @Override
  public Stream<Metadata> visitBasicAuth(BasicAuth basicAuth) {
    return Stream.of(
        buildAuthentication(basicAuth)
    );
  }

  @Override
  public Stream<Metadata> visitCertificateBasedBasicAuth(
      CertificateBasedBasicAuth certificateBasedBasicAuth) {
    return Stream.concat(
        visitBasicAuth(certificateBasedBasicAuth),
        Stream.of(buildCertificateAuthorityEmail(certificateBasedBasicAuth))
    );
  }

  private Metadata buildCertificateAuthorityEmail(CertificateBasedBasicAuth basicAuth) {
    return Metadata.builder()
        .name(CERTIFICATE_BASED_BASIC_AUTH_CA_EMAIL)
        .value(basicAuth.getCaEmail())
        .type("string")
        .build();
  }

  @Override
  public Stream<Metadata> visitOAuth2(OAuth2 oAuth2) {
    return Stream.of(buildAuthentication(oAuth2));
  }

  private Metadata buildAuthentication(Authentication authentication) {
    return Metadata.builder()
        .name(Keys.DATASOURCE_AUTHENTICATION)
        .value(authentication.getClass().getSimpleName())
        .type("string")
        .build();
  }
}
