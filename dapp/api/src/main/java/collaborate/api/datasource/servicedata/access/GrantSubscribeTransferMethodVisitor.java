package collaborate.api.datasource.servicedata.access;

import static collaborate.api.mail.EMailService.NOREPLY_THEBLOCKCHAINXDEV_COM;
import static java.nio.charset.StandardCharsets.UTF_8;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.access.model.PendingAccessRequest;
import collaborate.api.datasource.businessdata.access.PendingAccessRequestRepository;
import collaborate.api.datasource.businessdata.access.CreateServiceAccountService;
import collaborate.api.datasource.businessdata.access.CipherJwtService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.Nft;
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

@Slf4j
@RequiredArgsConstructor
public class GrantSubscribeTransferMethodVisitor implements TransferMethodVisitor<Void> {

  public static final String REQUEST_ACCESS_EMAIL_HTML_TEMPLATE = "html/accessRequest.html";
  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;
  private final String businessDataContractAddress;
  private final CipherJwtService cipherService;
  private final CreateServiceAccountService createServiceAccountService;
  private final EMailService eMailService;
  private final GrantSubscribeDAO grantAccessDAO;
  private final OrganizationService organizationService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;
  private final Nft nft;
  private final String requester;

  @Override
  public Void visitEmailNotification(EmailNotification emailNotification) {
    pendingAccessRequestRepository.save(new PendingAccessRequest(requester, nft.getNftId()));
    var organization = organizationService.getByWalletAddress(requester);
    EMailDTO emailDTO = new EMailDTO(
        NOREPLY_THEBLOCKCHAINXDEV_COM,
        emailNotification.getEmail(),
        "New access requested",
        Map.of(
            "organizationName", organization.getLegalName(),
            "organizationAddress", requester,
            "datasourceId", nft.getDatasourceId(),
            "nftId", nft.getNftId().toString(),
            "scope", nft.findScope().orElse("")
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
    var authentication = authenticationService
        .findAuthentication(nft.getDatasourceId())
        .orElseThrow(() -> new IllegalStateException(
            "Missing authentication for datasourceId=" + nft.getDatasourceId())
        );

    if (authentication instanceof OAuth2ClientCredentialsGrant) {
      var accessTokenResponse = accessTokenProvider.get(
          (OAuth2ClientCredentialsGrant) authentication,
          nft.findScope()
      );
      var cipheredToken = cipherService.cipher(accessTokenResponse.getAccessToken(), requester);
      grantAccessDAO.grantAccess(cipheredToken, requester, nft.getNftId());
    } else {
      var error = String.format("On datasourceId=%s, expects access method=%s, current is =%s ",
          nft.getDatasourceId(),
          OAuth2ClientCredentialsGrant.class.getName(),
          authentication.getClass().getName()
      );
      throw new IllegalStateException(error);
    }
    return null;
  }

  @Override
  public Void visitOAuth2ClientCredentials(OAuth2ClientCredentials clientCredentials) {
    // Get the access method credentials
    var authentication = authenticationService
        .findAuthentication(nft.getDatasourceId())
        .orElseThrow(() -> new IllegalStateException(
            "Missing authentication for datasourceId=" + nft.getDatasourceId())
        );
    var tokenEndpoint = ((OAuth2ClientCredentialsGrant) authentication).getTokenEndpoint();
    var accessTokenResponse = accessTokenProvider.get(
        (OAuth2ClientCredentialsGrant) authentication,
        Optional.empty()
    );

    // Call the registration URL using the JWT
    var serviceAccountResponse = createServiceAccountService.post(
        clientCredentials,
        nft.findScope(),
        accessTokenResponse.getAccessToken()
    );
    if (!serviceAccountResponse.getStatusCode().is2xxSuccessful()
        || serviceAccountResponse.getBody() == null) {
      log.error("Unexpected create service account response={}", serviceAccountResponse);
      throw new IllegalStateException("Unexpected create service account response");
    }
    var oAuth2 = OAuth2ClientCredentialsGrant.builder()
        .partnerTransferMethod(new OAuth2SharedCredentials())
        .tokenEndpoint(tokenEndpoint)
        .grantType("client_credentials")
        .clientId(serviceAccountResponse.getBody().getClientId())
        .clientSecret(serviceAccountResponse.getBody().getClientSecret())
        .build();

    // Store it in the Vault
    authenticationService.saveCredentials(
        businessDataContractAddress,
        requester,
        nft.getNftId(),
        oAuth2
    );

    // Send the grant transaction
    accessTokenResponse = accessTokenProvider.get(oAuth2, nft.findScope());
    var cipheredToken = cipherService.cipher(accessTokenResponse.getAccessToken(), requester);
    grantAccessDAO.grantAccess(cipheredToken, requester, nft.getNftId());

    return null;
  }

}
