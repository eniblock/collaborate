package collaborate.api.organization;

import static collaborate.api.organization.model.OrganizationRole.DSP;

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.cache.CacheService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.model.OrganizationStatus;
import collaborate.api.user.UserService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {CacheNames.ORGANIZATION})
public class OrganizationService {

  private final ApiProperties apiProperties;

  private final CacheService cacheService;
  private final OrganizationDAO organizationDAO;

  private final UserService userService;

  @Cacheable(key = "'all'")
  public Collection<OrganizationDTO> getAllOrganizations() {
    var knownOrganizations = organizationDAO.getAllOrganizations();

    var currentOrganization = getCurrentOrganization();
    var currentIsKnown = knownOrganizations.stream()
        .anyMatch(orga -> StringUtils.equals(currentOrganization.getAddress(), orga.getAddress()));
    if (!currentIsKnown){
      return Stream.concat(knownOrganizations.stream(), Stream.of(currentOrganization))
          .collect(Collectors.toList());
    }else{
      return knownOrganizations;
    }

  }

  @Cacheable(key = "'publicKey' + #publicKeyHash")
  public Optional<OrganizationDTO> findOrganizationByPublicKeyHash(String publicKeyHash) {
    return organizationDAO.findOrganizationByPublicKeyHash(publicKeyHash);
  }


  @Cacheable(key = "'wallet' + #walletAddress")
  public OrganizationDTO getByWalletAddress(String walletAddress) {
    return findOrganizationByPublicKeyHash(walletAddress)
        .orElseGet(() -> {
          log.warn("No organization found for account={}", walletAddress);
          return OrganizationDTO.builder().address(walletAddress).build();
        });
  }

  @Cacheable(key = "'current'")
  public OrganizationDTO getCurrentOrganization() {
    var adminUser = userService.getAdminUser();
    return findOrganizationByPublicKeyHash(adminUser.getAddress())
        .orElseGet(() -> OrganizationDTO.builder()
            .encryptionKey(apiProperties.getPublicEncryptionKey())
            .address(adminUser.getAddress())
            .legalName(apiProperties.getPlatform())
            .status(OrganizationStatus.INACTIVE)
            .build());
  }

  @Cacheable(key = "'dspWallets'")
  public List<String> getAllDspWallets() {
    return getAllOrganizations()
        .stream()
        .filter(o -> o.getRoles() != null)
        .filter(o -> o.getRoles().contains(DSP))
        .map(OrganizationDTO::getAddress)
        .collect(Collectors.toList());
  }

  public Optional<OrganizationDTO> findByLegalName(String legalName) {
    return getAllOrganizations().stream()
        .filter(o -> StringUtils.equals(o.getLegalName(), legalName))
        .findFirst();
  }

  public void clearCache(){
    cacheService.clearOrThrow(CacheNames.ORGANIZATION);
  }

  /**
   * Add an organization if the address it not already known<br> Otherwise the organization is
   * updated
   */
  public OrganizationDTO upsertOrganization(OrganizationDTO organization) {
    organizationDAO.upsert(organization);
    clearCache();
    log.debug("organization.legalName={} added", organization.getAddress());
    return organizationDAO.findOrganizationByPublicKeyHash(organization.getAddress())
        .orElseThrow(() -> new IllegalStateException("Inserted organization not found"));
  }

}
