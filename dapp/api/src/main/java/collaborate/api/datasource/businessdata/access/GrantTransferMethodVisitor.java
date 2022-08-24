package collaborate.api.datasource.businessdata.access;

import static collaborate.api.mail.EMailService.NOREPLY_THEBLOCKCHAINXDEV_COM;
import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.access.model.PendingAccessRequest;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.NftScope;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.dto.web.authentication.transfer.EmailNotification;
import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2ClientCredentials;
import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2SharedCredentials;
import collaborate.api.datasource.model.dto.web.authentication.transfer.TransferMethodVisitor;
import collaborate.api.mail.EMailDTO;
import collaborate.api.mail.EMailService;
import collaborate.api.organization.OrganizationService;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

@Slf4j
@RequiredArgsConstructor
public class GrantTransferMethodVisitor implements TransferMethodVisitor<Void> {

  public static final String REQUEST_ACCESS_EMAIL_HTML_TEMPLATE = "html/accessRequest.html";
  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;
  private final CipherJwtService cipherService;
  private final EMailService eMailService;
  private final GrantAccessDAO grantAccessDAO;
  private final OrganizationService organizationService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;
  private final NftScope nftScope;
  private final String requester;

  @Override
  public Void visitEmailNotification(EmailNotification emailNotification) {
    pendingAccessRequestRepository.save(new PendingAccessRequest(requester, nftScope.getNftId()));
    var organization = organizationService.getByWalletAddress(requester);
    EMailDTO emailDTO = new EMailDTO(
        NOREPLY_THEBLOCKCHAINXDEV_COM,
        emailNotification.getEmail(),
        "New access requested",
        Map.of(
            "organizationName", organization.getLegalName(),
            "organizationAddress", requester,
            "datasourceId", nftScope.getDatasourceId(),
            "nftId", nftScope.getNftId().toString(),
            "scope", Optional.ofNullable(nftScope.getScope()).orElse("")
        )
    );

    try {
      eMailService.sendMail(emailDTO, UTF_8.name(), REQUEST_ACCESS_EMAIL_HTML_TEMPLATE);
    } catch (MessagingException e) {
      log.error(emailDTO.toString());
      throw new IllegalStateException("E-mail not sent", e);
    }
    return null;
  }

  @Override
  public Void visitOAuth2SharedCredentials(OAuth2SharedCredentials oAuth2) {
    var authentication = authenticationService.getAuthentication(nftScope.getDatasourceId());

    if (authentication instanceof OAuth2ClientCredentialsGrant) {
      var accessTokenResponse = accessTokenProvider.get(
          (OAuth2ClientCredentialsGrant) authentication,
          Optional.ofNullable(nftScope.getScope())
      );
      var cipheredToken = cipherService.cipher(accessTokenResponse.getAccessToken(), requester);
      grantAccessDAO.grantAccess(cipheredToken, requester, nftScope.getNftId());
    } else {
      var error = String.format("On datasourceId=%s, expects access method=%s, current is =%s ",
          nftScope.getDatasourceId(),
          OAuth2ClientCredentialsGrant.class.getName(),
          authentication.getClass().getName()
      );
      throw new IllegalStateException(error);
    }
    return null;
  }

  @Override
  public Void visitOAuth2ClientCredentials(OAuth2ClientCredentials oAuth2) {
    // TODO COL-655
    throw new NotImplementedException("COL-655 Not implemented");
  }


}
