package collaborate.api.datasource.passport.find;

import static collaborate.api.datasource.passport.model.AccessStatus.GRANTED;
import static collaborate.api.datasource.passport.model.AccessStatus.LOCKED;
import static collaborate.api.datasource.passport.model.AccessStatus.PENDING;
import static java.lang.Boolean.TRUE;

import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.model.storage.Multisig;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DigitalPassportDetailsDTOFactory {

  private final ConnectedUserService connectedUserService;
  private final FindPassportDAO findPassportDAO;
  private final OrganizationService organizationService;
  private final UserService userService;
  private final CatalogService catalogService;

  public DigitalPassportDetailsDTO createFromMultisigContractid(Integer contractId,
      Multisig multisig) {
    return DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(catalogService.findByMultisig(multisig, contractId)
            .orElse(null)
        ).assetId(multisig.getParam1())
        .assetOwner(userService.getByWalletAddress(multisig.getAddr2()))
        .accessStatus(getAccessStatus(multisig))
        // TODO creationDatetime
        .creationDatetime(null)
        .operator(organizationService.getByWalletAddress(multisig.getAddr1()))
        .multisigContractId(contractId)
        .tokenId(findPassportDAO.findTokenIdByAssetId(multisig.getParam1())
            .orElse(null)
        )
        .tokenStatus(TokenStatus.from(multisig))
        .build();
  }

  AccessStatus getAccessStatus(Multisig multisig) {
    var connectedUserWalletAddress = connectedUserService.getWalletAddress();
    if (connectedUserWalletAddress.equals(multisig.getAddr2())) { // We are the NFT owner
      return GRANTED;
    } else if (multisig.getAddr1().equals(connectedUserWalletAddress)) { // We are the NFT operator
      if (TRUE.equals(multisig.getOk())) {
        return GRANTED;
      } else {
        return PENDING;
      }
    }
    return LOCKED; // We are someone else
  }

  public DigitalPassportDetailsDTO fromMultisig(TagEntry<Integer, Multisig> multisigEntry) {
    var multisig = multisigEntry.getValue();
    var contractId = multisigEntry.getKey();
    return DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(null)
        .assetId(multisig.getParam1())
        .assetOwner(userService.getByWalletAddress(multisig.getAddr2()))
        .accessStatus(PENDING)
        .creationDatetime(null) // No token => no creation datetime
        .operator(organizationService.getByWalletAddress(multisig.getAddr1()))
        .multisigContractId(contractId)
        .tokenId(null) // No token => no tokenid
        .tokenStatus(TokenStatus.PENDING_CREATION)
        .build();
  }


  public DigitalPassportDetailsDTO fromPassportsIndexerToken(
      TokenIndex passportsIndexerToken,
      String dspWalletAddress) {
    return DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(null)
        .assetId(passportsIndexerToken.getAssetId())
        .assetOwner(userService.getByWalletAddress(passportsIndexerToken.getTokenOwnerAddress()))
        .accessStatus(
            makeAccessStatus(
                passportsIndexerToken.getTokenOwnerAddress(),
                dspWalletAddress))
        // TODO creationDatetime
        .creationDatetime(null)
        .operator(
            organizationService.getByWalletAddress(dspWalletAddress)
        )
        .multisigContractId(null) // No need to get the multisig contract id
        .tokenId(passportsIndexerToken.getTokenId())
        .tokenStatus(TokenStatus.CREATED)
        .build();
  }

  private AccessStatus makeAccessStatus(String nftOwnerAddress, String operatorAddress) {
    var connectedUserWalletAddress = connectedUserService.getWalletAddress();
    if (connectedUserWalletAddress.equals(nftOwnerAddress) || connectedUserWalletAddress.equals(
        operatorAddress)) {
      return GRANTED;
    }
    return LOCKED;
  }

  public DigitalPassportDetailsDTO createFromPassportIndexer(String dspAddress,
      TokenIndex indexerToken) {
    var tokenMetadata = findPassportDAO.findTokenMetadataByTokenId(indexerToken.getTokenId())
        .orElseThrow(() -> new IllegalStateException(
            "No tokenMetadata found for tokenId=" + indexerToken.getTokenId())
        );
    var assetDataCatalogDTO = catalogService.findByIpfsLink(tokenMetadata.getIpfsUri());
    var digitalPassortsDetails = fromPassportsIndexerToken(indexerToken, dspAddress);
    digitalPassortsDetails.setAssetDataCatalog(assetDataCatalogDTO.orElse(null));
    return digitalPassortsDetails;
  }

}
