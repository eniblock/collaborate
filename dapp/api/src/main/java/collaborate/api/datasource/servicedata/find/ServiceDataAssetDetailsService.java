package collaborate.api.datasource.servicedata.find;

import collaborate.api.comparator.SortComparison;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.servicedata.ServiceDataNftService;
import collaborate.api.datasource.servicedata.transaction.ServiceDataTransactionService;
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

  private final AuthenticationService authenticationService;
  private final ServiceDataNftIndexerService serviceDataNftIndexerService;
  private final String serviceDataContractAddress;
  private final ServiceDataTransactionService serviceDataTransactionService;
  private final ServiceDataNftService nftService;
  private final OrganizationService organizationService;
  private final KpiService kpiService;
  private final SortComparison sortComparison;

  /**
   * @deprecated Use {@link #toAssetDetails(Nft)} instead
   */
  @Deprecated(since = "0.6.0")
  AssetDetailsDTO toAssetDetails(TokenIndex t) {
    var datasourceId = StringUtils.substringBefore(t.getAssetId(), ":");
    var alias = StringUtils.substringAfter(t.getAssetId(), ":");
    var creationDate = serviceDataTransactionService
        .findTransactionDateByTokenId(
            serviceDataContractAddress,
            t.getAssetId()
        );
    return AssetDetailsDTO.builder()
        .accessStatus(getAccessStatus(datasourceId, t.getTokenId()))
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(AssetDetailsDatasourceDTO.builder()
                    .id(datasourceId)
                    .assetIdForDatasource(alias)
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

  public AccessStatus getAccessStatus(String datasourceId, Integer tokenId) {
    if (authenticationService.isGranted(datasourceId, tokenId, serviceDataContractAddress, organizationService.getCurrentOrganization().getAddress())) {
      return AccessStatus.GRANTED;
    } else {
      return AccessStatus.LOCKED;
    }
  }

  public Page<AssetDetailsDTO> find(Pageable pageable, Optional<Predicate<TokenIndex>> predicate,
      Optional<String> assetOwner) {
    var filteredAssetDetails = serviceDataNftIndexerService.find(predicate, assetOwner)
        .stream()
        .map(this::toAssetDetails)
        .collect(Collectors.toList());

    var assetDetails = sortComparison.sorted(
            filteredAssetDetails.stream(), pageable.getSort(), AssetDetailsDTO.class)
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .collect(Collectors.toList());
    return new PageImpl<>(assetDetails, pageable, filteredAssetDetails.size());
  }

  public Page<AssetDetailsDTO> marketPlace(Map<String, String> filters, Pageable pageable) {
    return nftService.findMarketPlaceByFilters(filters, pageable)
        .map(this::toAssetDetails);
  }

  AssetDetailsDTO toAssetDetails(Nft t) {
    var datasourceId = t.getDatasourceId();
    var alias = t.getAssetId().getAlias();
    var creationDate = serviceDataTransactionService
        .findTransactionDateByTokenId(
            serviceDataContractAddress,
            t.getAssetId().toString()
        );
    return AssetDetailsDTO.builder()
        .accessStatus(getAccessStatus(datasourceId, t.getNftId()))
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
        .creationDatetime(creationDate.orElse(null))
        .grantedAccess(kpiService.count(new KpiSpecification("nft-id", t.getNftId().toString())))
        .build();
  }
}
