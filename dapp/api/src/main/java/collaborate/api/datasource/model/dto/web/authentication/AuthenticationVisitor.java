package collaborate.api.datasource.model.dto.web.authentication;

public interface AuthenticationVisitor<T> {

  T visitBasicAuth(BasicAuth basicAuth);

  T visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth);

  T visitOAuth2(OAuth2 oAuth2);
}