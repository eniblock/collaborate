package collaborate.api.passport.find;

import static collaborate.api.passport.model.AccessStatus.GRANTED;
import static collaborate.api.passport.model.AccessStatus.NO_ACCESS;
import static collaborate.api.passport.model.AccessStatus.PENDING;
import static java.lang.Boolean.TRUE;

import collaborate.api.organization.OrganizationService;
import collaborate.api.passport.model.AccessStatus;
import collaborate.api.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.passport.model.TokenStatus;
import collaborate.api.passport.model.storage.Multisig;
import collaborate.api.passport.model.storage.PassportsIndexerToken;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.UserService;
import java.util.Arrays;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DigitalPassportDetailsDTOFactory {

  private final FindPassportDAO findPassportDAO;
  private final TokenMetadataService tokenMetadataService;
  private final UserService userService;
  private final OrganizationService organizationService;

  public DigitalPassportDetailsDTO createFromMultisigContractid(Integer contractId,
      Multisig multisig) {
    return DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(tokenMetadataService.findDataCatalog(multisig, contractId)
            .orElse(null)
        ).assetId(multisig.getParam1())
        .assetOwner(userService.buildUserWalletDTO(multisig.getAddr2()))
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
    var connectedUserWalletAddress = userService.getConnectedUserWallet().getAddress();
    if (connectedUserWalletAddress.equals(multisig.getAddr2())) {
      return GRANTED;
    } else if (multisig.getAddr1().equals(connectedUserWalletAddress)) {
      if (TRUE.equals(multisig.getOk())) {
        return GRANTED;
      } else {
        return PENDING;
      }
    }
    return NO_ACCESS;
  }

  public DigitalPassportDetailsDTO fromMultisig(TagEntry<Integer, Multisig> multisigEntry) {
    var multisig = multisigEntry.getValue();
    var contractId = multisigEntry.getKey();
    return DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(null)
        .assetId(multisig.getParam1())
        .assetOwner(UserWalletDTO.builder()
            .address(multisig.getAddr2())
            .build()
        )
        .accessStatus(null) // No token => no access status
        .creationDatetime(null) // No token => no creation datetime
        .operator(organizationService.getByWalletAddress(multisig.getAddr1()))
        .multisigContractId(contractId)
        .tokenId(null) // No token => no tokenid
        .tokenStatus(TokenStatus.PENDING_CREATION)
        .build();
  }


  public DigitalPassportDetailsDTO fromPassportsIndexerToken(
      PassportsIndexerToken passportsIndexerToken,
      String dspWalletAddress) {
    return DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(null)
        .assetId(passportsIndexerToken.getAssetId())
        .assetOwner(UserWalletDTO.builder()
            .address(passportsIndexerToken.getTokenOwnerAddress())
            .build()
        )
        // TODO accessStatus
        .accessStatus(getAccessStatusByAssetId(passportsIndexerToken.getAssetId()))
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

  private AccessStatus getAccessStatusByAssetId(String assetId) {
    // FIXME
    log.error("Not implemented");
    return null;
  }

  public DigitalPassportDetailsDTO loadFullDetails(DigitalPassportDetailsDTO digitalPassport) {
    // Load assetOwner data
    var assetOwner = userService.buildUserWalletDTO(digitalPassport.getAssetOwner().getAddress());
    digitalPassport.setAssetOwner(assetOwner);

    // Load asset data catalog
    var tokenMetadataOpt = findPassportDAO
        .findTokenMetadataByTokenIds(Arrays.asList(digitalPassport.getTokenId()))
        .getTokenMetadata()
        .stream().findFirst()
        .map(TagEntry::getValue);
    var assetDataCatalog =
        tokenMetadataOpt.map(
            tokenMetadata -> tokenMetadataService
                .findDataCatalog(tokenMetadata.getIpfsUri().toString())
        ).flatMap(Function.identity())
        .orElse(null);
    digitalPassport.setAssetDataCatalog(assetDataCatalog);

    // Load the creation datetime
    // TODO : load creationDatetime

    return digitalPassport;
  }
}