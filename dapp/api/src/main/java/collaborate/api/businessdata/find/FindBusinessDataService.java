package collaborate.api.businessdata.find;

import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import collaborate.api.passport.model.AccessStatus;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.passport.model.DatasourceDTO;
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

  public Collection<AssetDetailsDTO> getAll() {
    var dspWallets = organizationService.getAllDspWallets();
    var assetByDsp = findBusinessDataDAO.findPassportsIndexersByDsps(dspWallets);
    return assetByDsp.streamTokenIndexes()
        .map(this::toAssetDetails)
        .collect(Collectors.toList());
  }

  AssetDetailsDTO toAssetDetails(TokenIndex t) {

    return AssetDetailsDTO.builder()
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(DatasourceDTO.builder()
                    .id(StringUtils.substringBefore(t.getAssetId(), ":"))
                    .assetIdForDatasource(StringUtils.substringAfter(t.getAssetId(), ":"))
                    .build()
                ))
                .build()
        ).assetOwner(organizationService.getByWalletAddress(t.getTokenOwnerAddress()))
        .assetId(StringUtils.substringAfter(t.getAssetId(), ":"))
        .tokenId(t.getTokenId())
        .accessStatus(AccessStatus.LOCKED)
        .build();
  }
}
