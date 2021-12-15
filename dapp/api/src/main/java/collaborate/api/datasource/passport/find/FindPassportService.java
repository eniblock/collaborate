package collaborate.api.datasource.passport.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.storage.PassportsIndexer;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindPassportService {

  private final DigitalPassportDetailsDTOFactory digitalPassportDetailsDTOFactory;
  private final ConnectedUserService connectedUserService;
  private final FindPassportDAO findPassportDAO;
  private final OrganizationService organizationService;
  private final UserService userService;

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsFromMultisig(Integer contractId) {
    return findPassportDAO.findMultisigById(contractId)
        .map(multisig ->
            digitalPassportDetailsDTOFactory.createFromMultisigContractid(contractId, multisig)
        );
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsByTokenId(Integer tokenId) {
    return findDspAndPassportIndexerTokenByTokenId(tokenId)
        .map(t -> digitalPassportDetailsDTOFactory
            .createFromPassportIndexer(t.getKey(), t.getValue()));
  }

  public List<DigitalPassportDetailsDTO> findPassportDetailsByTokenIdList(
      List<Integer> tokenIdList) {
    return List.of();
  }

  /**
   * @return An optional entry where key is the dspAddress and value the {@link TokenIndex}
   */
  Optional<SimpleEntry<String, TokenIndex>> findDspAndPassportIndexerTokenByTokenId(
      Integer tokenId) {
    var dspAddresses = organizationService.getAllDspWallets();

    return findPassportDAO.findPassportsIndexersByDsps(dspAddresses).getPassportsIndexerByDsp()
        .stream()
        .filter(oneEntry -> StringUtils.isEmpty(oneEntry.getError()))
        .filter(oneEntry -> oneEntry.getValue() != null)
        .map(dspTokenEntry -> buildPassportsIndexerByDspAddress(tokenId, dspTokenEntry))
        .filter(dspTokenEntry -> dspTokenEntry.getValue() != null)
        .findFirst();
  }

  private SimpleEntry<String, TokenIndex> buildPassportsIndexerByDspAddress(
      Integer tokenId, TagEntry<String, PassportsIndexer> dspTokenEntry) {
    return new SimpleEntry<>(dspTokenEntry.getKey(),
        dspTokenEntry.getValue().getTokens().stream()
            .filter(Objects::nonNull)
            .filter(token -> token.getTokenId().equals(tokenId))
            .findFirst()
            .orElse(null)
    );
  }

  public Collection<DigitalPassportDetailsDTO> getByConnectedUser() {
    Collection<DigitalPassportDetailsDTO> digitalPassports;

    Set<String> roles = connectedUserService.getRealmRoles();
    if (roles.contains(ASSET_OWNER)) {
      digitalPassports = getAllPassportsByAssetOwner(connectedUserService.getEmailOrThrow());
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
    var assetOwnerAddress = userService.findWalletAddressByEmailOrThrow(assetOwnerEmail);
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

    var multisigContractIds = passportsIndexerDto.getPassportsIndexerByDsp().stream()
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

    return passportsIndexerDto.getPassportsIndexerByDsp().stream()
        .filter(e -> e.getValue() != null)
        .map(e -> e.getValue().getTokens().stream()
            .filter(t -> vehiculeOwnerAddressFilter.isEmpty()
                || t.getTokenOwnerAddress().equals(vehiculeOwnerAddressFilter.get())
            ).map(token -> digitalPassportDetailsDTOFactory
                .fromPassportsIndexerToken(token, e.getKey())
            )
        ).flatMap(Stream::distinct)
        .collect(toMap(DigitalPassportDetailsDTO::getTokenId, identity()));
  }

}
