package collaborate.api.organization;

import collaborate.api.organization.model.UpdateOrganizationTypeDTO;
import collaborate.api.organization.tag.Organization;
import collaborate.api.user.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PendingOrganizationService {

  public static final int ONE_XTZ = 1000000;
  private final OrganizationService organizationService;

  private final PendingOrganizationRepository pendingOrganizationRepository;

  private final UserService userService;

  public void clearCache() {
    organizationService.clearCache();
  }

  List<String> findKnownPendingAddresses(List<UpdateOrganizationTypeDTO> updatesOrRemoveOrgs) {
    var addresses = updatesOrRemoveOrgs.stream()
        .map(UpdateOrganizationTypeDTO::getAddress)
        .collect(Collectors.toList());

    return pendingOrganizationRepository.findAllById(addresses).stream()
        .map(Organization::getAddress)
        .collect(Collectors.toList());
  }

  @Transactional
  public void removePendings(List<UpdateOrganizationTypeDTO> updatesOrRemoveOrgs) {
    var knownPendings = findKnownPendingAddresses(updatesOrRemoveOrgs);
    if (!knownPendings.isEmpty()) {
      pendingOrganizationRepository.deleteByAddressIn(knownPendings);
      clearCache();
    }
  }

  void activatePendingWallets(List<UpdateOrganizationTypeDTO> updatesOrRemoveOrgs) {
    findKnownPendingAddresses(updatesOrRemoveOrgs)
        .forEach(orgAddress ->
            userService.transferMutez(UserService.ORGANIZATION_USER_ID, orgAddress, ONE_XTZ)
        );
  }

}
