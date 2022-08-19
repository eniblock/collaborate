package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.dto.web.authentication.transfer.CertificateBasedAuthorityEmail;
import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2SharedCredentials;
import collaborate.api.datasource.model.dto.web.authentication.transfer.TransferMethodVisitor;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AccessTokenTransferMethodVisitor implements TransferMethodVisitor<Void> {

  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;
  private final String datasourceId;
  private final String scope;
  private final Consumer<String> sendGrantAccessTransaction;

  @Override
  public Void visitCertificateBasedBasicAuth(CertificateBasedAuthorityEmail email) {
    // TODO COL-656
    return null;
  }

  @Override
  public Void visitOAuth2SharedCredentials(OAuth2SharedCredentials oAuth2) {
    var authentication = authenticationService.getAuthentication(datasourceId);

    if (authentication instanceof OAuth2ClientCredentialsGrant) {
      var accessTokenResponse = accessTokenProvider.get(
          (OAuth2ClientCredentialsGrant) authentication,
          Optional.ofNullable(scope)
      );
      sendGrantAccessTransaction.accept(accessTokenResponse.getAccessToken());
    } else {
      var error = String.format("On datasourceId=%s, expects access method=%s, current is =%s ",
          datasourceId,
          OAuth2ClientCredentialsGrant.class.getName(),
          authentication.getClass().getName()
      );
      throw new IllegalStateException(error);
    }
    return null;
  }


}
