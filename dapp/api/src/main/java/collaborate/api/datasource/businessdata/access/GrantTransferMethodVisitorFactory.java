package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.Nft;
import collaborate.api.mail.EMailService;
import collaborate.api.organization.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrantTransferMethodVisitorFactory {

  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;

  private final String businessDataContractAddress;
  private final CipherJwtService cipherService;

  private final CreateServiceAccountService createServiceAccountService;
  private final EMailService eMailService;
  private final GrantAccessDAO grantAccessDAO;
  private final OrganizationService organizationService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;

  public GrantTransferMethodVisitor create(Nft nft, String requester) {
    return new GrantTransferMethodVisitor(
        accessTokenProvider,
        authenticationService,
        businessDataContractAddress,
        cipherService,
        createServiceAccountService,
        eMailService,
        grantAccessDAO,
        organizationService,
        pendingAccessRequestRepository,
        nft,
        requester
    );
  }

}
