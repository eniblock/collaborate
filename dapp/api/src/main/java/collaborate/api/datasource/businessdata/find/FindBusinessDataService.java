package collaborate.api.datasource.businessdata.find;

import collaborate.api.datasource.businessdata.transaction.BusinessDataTransactionService;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindBusinessDataService {

  private final String businessDataContractAddress;
  private final BusinessDataTransactionService businessDataTransactionService;
  private final FindBusinessDataDAO findBusinessDataDAO;
  private final OrganizationService organizationService;
  private final UserMetadataService userMetadataService;

  public Collection<AssetDetailsDTO> find(Pageable pageable, Optional<String> query,
      Optional<String> ownerAddress) {
    var dspWallets = ownerAddress.map(List::of)
        .orElse(organizationService.getAllDspWallets());

    var assetByDsp = findBusinessDataDAO.findPassportsIndexersByDsps(dspWallets);
    var tokenIndexStream =  assetByDsp.streamTokenIndexes()
        .skip(pageable.getOffset())

        .map(this::toAssetDetails)
        .collect(Collectors.toList());
  }

  AssetDetailsDTO toAssetDetails(TokenIndex t) {
    var datasourceId = StringUtils.substringBefore(t.getAssetId(), ":");
    var assetIdForDatasource = StringUtils.substringAfter(t.getAssetId(), ":");
    var creationDate = businessDataTransactionService
        .findTransactionDateByTokenId(
            businessDataContractAddress,
            t.getAssetId()
        );
    return AssetDetailsDTO.builder()
        .accessStatus(getAccessStatus(datasourceId, assetIdForDatasource))
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(AssetDetailsDatasourceDTO.builder()
                    .id(datasourceId)
                    .assetIdForDatasource(assetIdForDatasource)
                    .ownerAddress(t.getTokenOwnerAddress())
                    .build()
                ))
                .build()
        ).assetOwner(organizationService.getByWalletAddress(t.getTokenOwnerAddress()))
        .assetId(t.getAssetId())
        .tokenId(t.getTokenId())
        .tokenStatus(TokenStatus.CREATED)
        .creationDatetime(creationDate.orElse(null))
        .build();
  }

  public AccessStatus getAccessStatus(String datasourceId, String scope) {
    var oAuthScope = StringUtils.removeStart(scope, "scope:");
    if (hasAccessStatus(datasourceId, oAuthScope)) {
      return AccessStatus.GRANTED;
    } else {
      return AccessStatus.LOCKED;
    }
  }

  private boolean hasAccessStatus(String datasourceId, String oAuthScope) {
    return userMetadataService.getOwnerOAuth2(datasourceId).isPresent()
        || userMetadataService.getRequesterAccessToken(datasourceId, oAuthScope).isPresent();
  }

}
