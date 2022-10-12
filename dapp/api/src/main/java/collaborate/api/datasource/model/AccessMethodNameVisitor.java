package collaborate.api.datasource.model;

import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import org.springframework.stereotype.Component;

@Component
public class AccessMethodNameVisitor implements AuthenticationVisitor<String> {

  @Override
  public String visitBasicAuth(BasicAuth basicAuth) {
    return BasicAuth.TYPE_NAME;
  }

  @Override
  public String visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    return CertificateBasedBasicAuth.TYPE_NAME;
  }

  @Override
  public String visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    return OAuth2ClientCredentialsGrant.TYPE_NAME;
  }
}
