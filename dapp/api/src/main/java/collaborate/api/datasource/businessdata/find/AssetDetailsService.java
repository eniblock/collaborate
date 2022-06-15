package collaborate.api.datasource.businessdata.find;

import collaborate.api.datasource.businessdata.transaction.BusinessDataTransactionService;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.kpi.KpiSpecification;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AssetDetailsService {

  private final String businessDataContractAddress;
  private final BusinessDataTransactionService businessDataTransactionService;
  private final OrganizationService organizationService;
  private final UserMetadataService userMetadataService;
  private final KpiService kpiService;

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
        .grantedAccess(kpiService.count(new KpiSpecification("nft-id", t.getTokenId().toString())))
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

  public boolean hasAccessStatus(String datasourceId, String oAuthScope) {
    return userMetadataService.getOwnerOAuth2(datasourceId).isPresent()
        || userMetadataService.getRequesterAccessToken(datasourceId, oAuthScope).isPresent();
  }
}
