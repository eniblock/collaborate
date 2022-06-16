package collaborate.api.organization;

import static collaborate.api.organization.model.OrganizationRole.DSP;

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.user.UserService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames={CacheNames.ORGANIZATION})
public class OrganizationService {

  private final String organizationYellowPageContractAddress;
  private final OrganizationDAO organizationDAO;
  private final UserService userService;

  @Cacheable
  public Collection<OrganizationDTO> getAllOrganizations() {
    return organizationDAO.getAllOrganizations();
  }

  @Cacheable
  public Optional<OrganizationDTO> findOrganizationByPublicKeyHash(String publicKeyHash) {
    return organizationDAO.findOrganizationByPublicKeyHash(publicKeyHash);
  }

  @Cacheable
  public OrganizationDTO getByWalletAddress(String walletAddress) {
    return findOrganizationByPublicKeyHash(walletAddress)
        .orElseGet(() -> {
          log.warn("No organization found for account={}", walletAddress);
          return OrganizationDTO.builder().address(walletAddress).build();
        });
  }

  @Cacheable
  public OrganizationDTO getCurrentOrganization() {
    var adminUser = userService.getAdminUser();
    return getByWalletAddress(adminUser.getAddress());
  }

  @Cacheable
  public List<String> getAllDspWallets() {
    return getAllOrganizations()
        .stream()
        .filter(o -> o.getRoles() != null)
        .filter(o -> o.getRoles().contains(DSP))
        .map(OrganizationDTO::getAddress)
        .collect(Collectors.toList());
  }
}
