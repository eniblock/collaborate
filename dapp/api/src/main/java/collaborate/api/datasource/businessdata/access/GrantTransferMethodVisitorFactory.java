package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.NftScope;
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
  private final CipherJwtService cipherService;
  private final EMailService eMailService;
  private final GrantAccessDAO grantAccessDAO;
  private final OrganizationService organizationService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;

  public GrantTransferMethodVisitor create(NftScope nftScope, String requester) {
    return new GrantTransferMethodVisitor(
        accessTokenProvider,
        authenticationService,
        cipherService,
        eMailService,
        grantAccessDAO,
        organizationService,
        pendingAccessRequestRepository,
        nftScope,
        requester
    );
  }

}
