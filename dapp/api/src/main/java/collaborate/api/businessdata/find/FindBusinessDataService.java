package collaborate.api.businessdata.find;

import static collaborate.api.businessdata.document.ScopeAssetsService.ASSET_ID_SEPARATOR;

import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import collaborate.api.passport.model.AccessStatus;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.passport.model.TokenStatus;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
    var access = getOwnerOAuth2(datasourceId)
        .or(() -> getRequesterAccessToken(datasourceId, oAuthScope));
    if (access.isPresent()) {
      return AccessStatus.GRANTED;
    } else {
      return AccessStatus.LOCKED;
    }
  }

  Optional<Object> getOwnerOAuth2(String datasourceId) {
    return userMetadataService.find(datasourceId, VaultMetadata.class)
        .filter(VaultMetadata::hasOAuth2)
        .map(VaultMetadata::getOAuth2);
  }

  private Optional<Object> getRequesterAccessToken(String datasourceId, String scope) {
    return userMetadataService
        .find(datasourceId + ASSET_ID_SEPARATOR + scope, VaultMetadata.class)
        .filter(VaultMetadata::hasJwt)
        .map(VaultMetadata::getJwt);
  }
}
