package collaborate.api.datasource.passport.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.storage.PassportsIndexer;
import collaborate.api.organization.OrganizationService;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    // 1) récupérer dans les du multisig dans Proxy :  l'adresse ipfs, @alice, @dsp
    // 2) récupérer les info ipfs
    // 3) nourir le DTO


    return findPassportDAO.findMultisigById(contractId)
        .map(multisig ->
            digitalPassportDetailsDTOFactory.createFromMultisigContractid(contractId, multisig)
        );
  }

  public List<DigitalPassportDetailsDTO> findPassportDetailsByTokenIdList(
      Collection<Long> tokenIdList) {
    // 1) récuperer l'adresse ipfs des tokens
    // 2) récuperer les metadatas dans ipfs
    // 3) récupérer les @alice dans les indexers (map, avec clef tokenId)
    // 4) récupérer les @dsp dans les indexers (map, avec clef tokenId)

    return List.of();
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsByTokenId(Integer tokenId) {
    return findDspAndPassportIndexerTokenByTokenId(tokenId)
        .map(t -> digitalPassportDetailsDTOFactory
            .createFromPassportIndexer(t.getKey(), t.getValue()));
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
      var connectedUserEmail = connectedUserService.getEmailOrThrow();
      var connectedUserWallet = userService.findWalletAddressByEmailOrThrow(connectedUserEmail);
      var tokenIds = findPassportDAO.getOwnerTokenIds(connectedUserWallet);
      digitalPassports = findPassportDetailsByTokenIdList(tokenIds);
    } else {
      var tokenIds = findAllTokenIds();
      digitalPassports = findPassportDetailsByTokenIdList(tokenIds);
    }

    return digitalPassports;
  }

  private Collection<Long> findAllTokenIds() {
    var allTokens = findPassportDAO.count();
    var tokenIds = new LinkedList<Long>();
    for (long i = 0; i < allTokens; i++) {
      tokenIds.add(i);
    }
    return tokenIds;
  }

  public long count() {
    return findPassportDAO.count();
  }


}
