package collaborate.api.datasource.model.dto.web.authentication.transfer;

public interface TransferMethodVisitor<T> {

  T visitEmailNotification(EmailNotification email);

  T visitOAuth2SharedCredentials(OAuth2SharedCredentials oAuth2);

  T visitOAuth2ClientCredentials(OAuth2ClientCredentials oAuth2);
}
