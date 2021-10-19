package collaborate.api.datasource.create;

import static collaborate.api.datasource.model.dto.web.authentication.BasicAuth.CONFIGURATION_REQUIRED_KEY;

import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationProviderMetadataVisitor implements
    AuthenticationVisitor<Set<Attribute>> {

  @Override
  public Set<Attribute> visitBasicAuth(BasicAuth basicAuth) {
    return Set.of(Attribute.builder()
        .name(CONFIGURATION_REQUIRED_KEY)
        .value(Boolean.TRUE.toString())
        .type("Boolean")
        .build()
    );
  }

  @Override
  public Set<Attribute> visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    return visitBasicAuth(basicAuth);
  }

  @Override
  public Set<Attribute> visitOAuth2(OAuth2 oAuth2) {
    return Collections.emptySet();
  }
}
