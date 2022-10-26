package collaborate.api.datasource.servicedata.find;

import collaborate.api.comparator.SortComparison;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.servicedata.nft.ServiceDataNftService;
//import collaborate.api.datasource.servicedata.transaction.ServiceDataTransactionService;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.kpi.KpiSpecification;
import collaborate.api.datasource.model.Nft;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ServiceDataAssetDetailsService {

  private final ServiceDataNftIndexerService serviceDataNftIndexerService;
  private final String serviceDataContractAddress;
  //private final ServiceDataTransactionService serviceDataTransactionService;
  private final ServiceDataNftService nftService;
  private final OrganizationService organizationService;
  private final KpiService kpiService;
  private final SortComparison sortComparison;

  public Page<AssetDetailsDTO> marketPlace(Map<String, String> filters, Pageable pageable) {
    return nftService.findMarketPlaceByFilters(filters, pageable)
        .map(this::toAssetDetails);
  }

  AssetDetailsDTO toAssetDetails(Nft t) {
    var datasourceId = t.getDatasourceId();
    var alias = t.getAssetId().getAlias();
    //var creationDate = serviceDataTransactionService.findTransactionDateByTokenId(serviceDataContractAddress, t.getAssetId().toString());
    return AssetDetailsDTO.builder()
        //.accessStatus(getAccessStatus(datasourceId, t.getNftId()))
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(AssetDetailsDatasourceDTO.builder()
                    .id(datasourceId)
                    .assetIdForDatasource(alias)
                    .ownerAddress(t.getOwnerAddress())
                    .build()
                ))
                .build()
        ).assetOwner(Optional.ofNullable(t.getOwnerAddress())
            .map(organizationService::getByWalletAddress)
            .orElseGet(organizationService::getCurrentOrganization))
        .assetId(t.getAssetId().toString())
        .tokenId(t.getNftId())
        .tokenStatus(t.getStatus())
        //.creationDatetime(creationDate.orElse(null))
        //.grantedAccess(kpiService.count(new KpiSpecification("nft-id", t.getNftId().toString())))
        .build();
  }
}
