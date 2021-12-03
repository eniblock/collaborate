package collaborate.api.datasource.businessdata.find;

import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.AssetDataCatalogDTO;
import collaborate.api.datasource.passport.model.DatasourceDTO;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindBusinessDataService {

  private final FindBusinessDataDAO findBusinessDataDAO;
  private final OrganizationService organizationService;
  private final UserMetadataService userMetadataService;

  public Collection<AssetDetailsDTO> getAll() {
    var dspWallets = organizationService.getAllDspWallets();
    var assetByDsp = findBusinessDataDAO.findPassportsIndexersByDsps(dspWallets);
    return assetByDsp.streamTokenIndexes()
        .map(this::toAssetDetails)
        .collect(Collectors.toList());
  }

  AssetDetailsDTO toAssetDetails(TokenIndex t) {
    var datasourceId = StringUtils.substringBefore(t.getAssetId(), ":");
    var assetIdForDatasource = StringUtils.substringAfter(t.getAssetId(), ":");
    return AssetDetailsDTO.builder()
        .accessStatus(getAccessStatus(datasourceId, assetIdForDatasource))
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(DatasourceDTO.builder()
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