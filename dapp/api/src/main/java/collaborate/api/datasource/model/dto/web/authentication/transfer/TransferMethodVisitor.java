package collaborate.api.datasource.model.dto.web.authentication.transfer;

public interface TransferMethodVisitor<T> {

  T visitCertificateBasedBasicAuth(CertificateBasedAuthorityEmail email);

  T visitOAuth2SharedCredentials(OAuth2SharedCredentials oAuth2);
}
