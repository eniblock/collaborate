package collaborate.api.passport.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.security.ConnectedUserDAO;
import collaborate.api.user.tag.TagUserDAO;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class FindPassportService {

  private final DigitalPassportDetailsDTOFactory digitalPassportDetailsDTOFactory;
  private final ConnectedUserDAO connectedUserDAO;
  private final FindPassportDAO findPassportDAO;
  private final OrganizationService organizationService;
  private final TagUserDAO tagUserDAO;

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsFromMultisig(Integer contractId) {
    return findPassportDAO.findMultisigById(contractId)
        .map(multisig ->
            digitalPassportDetailsDTOFactory.createFromMultisigContractid(contractId, multisig)
        );
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsByTokenId(Integer tokenId) {
    return getAllPassports(Optional.empty()).stream()
        .filter(p -> p.getTokenId().equals(tokenId))
        .findFirst()
        .stream()
        .map(digitalPassportDetailsDTOFactory::loadFullDetails)
        .findFirst();
  }

  public Collection<DigitalPassportDetailsDTO> getByConnectedUser() {
    Collection<DigitalPassportDetailsDTO> digitalPassports;

    Set<String> roles = connectedUserDAO.getRealmRoles();
    if (roles.contains(ASSET_OWNER)) {
      digitalPassports = getAllPassportsByAssetOwner(connectedUserDAO.getEmailOrThrow());
    } else {
      digitalPassports = getAllPassports(Optional.empty());
    }

    return digitalPassports;
  }

  public long count() {
    return findPassportDAO.count();
  }

  private Collection<DigitalPassportDetailsDTO> getAllPassportsByAssetOwner(
      String assetOwnerEmail) {
    var assetOwnerAddress = tagUserDAO.findOneByUserEmail(assetOwnerEmail)
        .map(UserWalletDTO::getAddress)
        .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR,
            "No vehicleOwner account found for current user=" + assetOwnerEmail));
    return getAllPassports(Optional.of(assetOwnerAddress));
  }

  private Collection<DigitalPassportDetailsDTO> getAllPassports(
      Optional<String> vehiculeOwnerAddressFilter) {
    var dspAddresses = organizationService.getAllOrganizations().stream()
        .map(OrganizationDTO::getAddress)
        .collect(Collectors.toList());
    return getAllPassportsByDsps(dspAddresses, vehiculeOwnerAddressFilter);
  }

  /**
   * @param vehiculeOwnerAddressFilter null if we don't want filter the results by vehiculeOwner
   */
  private Collection<DigitalPassportDetailsDTO> getAllPassportsByDsps(
      Collection<String> dspAddresses,
      Optional<String> vehiculeOwnerAddressFilter) {
    var passportsIndexer = findPassportDAO.findPassportsIndexersByDsps(dspAddresses);
    return getPassportsFromPassportsIndexers(passportsIndexer, vehiculeOwnerAddressFilter);
  }

  /**
   * @param vehiculeOwnerAddressFilter empty if we don't want filter the results by vehiculeOwner
   */
  private Collection<DigitalPassportDetailsDTO> getPassportsFromPassportsIndexers(
      PassportsIndexerTagResponseDTO passportsIndexerDto,
      Optional<String> vehiculeOwnerAddressFilter) {
    if (passportsIndexerDto == null) {
      return Collections.emptyList();
    }

    var waitingConsentAssets = buildWaitingConsentAssets(passportsIndexerDto,
        vehiculeOwnerAddressFilter);

    var digitalPassportsByTokenId = buildAssetByTokenId(passportsIndexerDto,
        vehiculeOwnerAddressFilter);

    var result = new LinkedList<DigitalPassportDetailsDTO>();
    result.addAll(digitalPassportsByTokenId.values());
    result.addAll(waitingConsentAssets);
    return result;
  }

  private Set<DigitalPassportDetailsDTO> buildWaitingConsentAssets(
      PassportsIndexerTagResponseDTO passportsIndexerDto,
      Optional<String> vehiculeOwnerAddressFilter) {

    var multisigContractIds = passportsIndexerDto.getPassportsIndexerList().stream()
        .filter(tagEntry -> tagEntry.getValue() != null)
        .flatMap(tagEntry -> tagEntry.getValue().getUnsignedMultisigs().stream())
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    return findPassportDAO.findMultisigByIds(multisigContractIds)
        .getMultisigs().stream()
        .filter(entry -> vehiculeOwnerAddressFilter.isEmpty()
            || entry.getValue().getAddr2().equals(vehiculeOwnerAddressFilter.get())
        ).map(digitalPassportDetailsDTOFactory::fromMultisig)
        .collect(Collectors.toSet());
  }

  private Map<Integer, DigitalPassportDetailsDTO> buildAssetByTokenId(
      PassportsIndexerTagResponseDTO passportsIndexerDto,
      Optional<String> vehiculeOwnerAddressFilter) {

    var digitalPassportsByTokenId = passportsIndexerDto.getPassportsIndexerList().stream()
        .filter(e -> e.getValue() != null)
        .map(e -> e.getValue().getTokens().stream()
            .filter(t -> vehiculeOwnerAddressFilter.isEmpty()
                || t.getTokenOwnerAddress().equals(vehiculeOwnerAddressFilter.get())
            ).map(token -> digitalPassportDetailsDTOFactory
                .fromPassportsIndexerToken(token, e.getKey())
            )
        ).flatMap(Stream::distinct)
        .collect(toMap(DigitalPassportDetailsDTO::getTokenId, identity()));

    var tokenMetadata = findPassportDAO
        .findTokenMetadataByTokenIds(digitalPassportsByTokenId.keySet())
        .getTokenMetadata();

    return digitalPassportsByTokenId;
  }

}