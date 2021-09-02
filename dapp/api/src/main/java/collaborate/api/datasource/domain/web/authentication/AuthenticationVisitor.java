package collaborate.api.datasource.domain.web.authentication;

public interface AuthenticationVisitor {

  void visitBasicAuth(BasicAuth basicAuth);

  void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) throws Exception;

  void visitOAuth2(OAuth2 oAuth2);
}