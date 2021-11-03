package collaborate.api.organization;

import static collaborate.api.organization.model.OrganizationRole.DSP;

import collaborate.api.config.api.ApiProperties;
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
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationService {

  private final ApiProperties apiProperties;
  private final OrganizationDAO organizationDAO;
  private final UserService userService;

  public Collection<OrganizationDTO> getAllOrganizations() {
    var organizationsFromPassportSC = organizationDAO.getAllOrganizations(
        apiProperties.getDigitalPassportContractAddress());
    var organizationsFromBusinessDataSC = organizationDAO.getAllOrganizations(
        apiProperties.getBusinessDataContractAddress());

    return Stream
        .concat(
            organizationsFromPassportSC.stream(),
            organizationsFromBusinessDataSC.stream()
        ).filter(distinctByKeyPredicate(OrganizationDTO::getAddress))
        .collect(Collectors.toList());
  }

  private <T> Predicate<T> distinctByKeyPredicate(Function<? super T, Object> keyExtractor) {
    Map<Object, Boolean> map = new ConcurrentHashMap<>();
    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  public Optional<OrganizationDTO> findOrganizationByPublicKeyHash(String publicKeyHash,
      String smartContractAddress) {
    return organizationDAO.findOrganizationByPublicKeyHash(publicKeyHash, smartContractAddress);
  }

  public OrganizationDTO getByWalletAddress(String walletAddress) {
    return findOrganizationByPublicKeyHash(
        walletAddress,
        apiProperties.getDigitalPassportContractAddress()
    ).orElseGet(() -> {
      log.warn("No organization found for account={}", walletAddress);
      return OrganizationDTO.builder().address(walletAddress).build();
    });
  }

  public OrganizationDTO getCurrentOrganizations() {
    var adminUser = userService.getAdminUser();
    return getByWalletAddress(adminUser.getAddress());
  }

  public List<String> getAllDspWallets() {
    return getAllOrganizations()
        .stream()
        .filter(o -> o.getRoles() != null)
        .filter(o -> o.getRoles().contains(DSP))
        .map(OrganizationDTO::getAddress)
        .collect(Collectors.toList());
  }
}
