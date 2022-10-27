package collaborate.api.datasource.servicedata.find;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

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
import collaborate.api.datasource.servicedata.model.ServiceData;
import collaborate.api.datasource.nft.TokenDAO;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.tag.model.TokenMetadata;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
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
  private final IpfsService ipfsService;
  private final TokenDAO tokenMetadataDAO;

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

  public ServiceData find(String assetId) {
    Optional<Nft> t = nftService.findById(assetId);
    if (t.isPresent()) {

        Integer tokenId = t.get().getNftId();

        var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, serviceDataContractAddress);
        
        var serviceData = tokenMedataOpt
          .map(TokenMetadata::getIpfsUri)
          .flatMap(this::findByIpfsLink)
          .orElseThrow(() -> new IllegalStateException(
              format("No metadata found for nftId=%d, smartContract=%s", tokenId, serviceDataContractAddress)
          ));

        return ServiceData.builder()
        .id(t.get().getAssetId().toString())
        .name(serviceData.getName())
        .description(serviceData.getDescription())
        .creationDatetime(serviceData.getCreationDatetime())
        .owner(serviceData.getOwner())
        .providerMetadata(serviceData.getProviderMetadata())
        .build();
     }
     return null;
  }

  public Optional<ServiceData> findByIpfsLink(String tZip21Url) {
    try {
      var tokenMetadata = ipfsService.cat(tZip21Url, ServiceData.class);
      return Optional.ofNullable(tokenMetadata);
    } catch (Exception e) {
      log.error("While getting data from tZip21Url={}\n{}", tZip21Url, e);
      return Optional.empty();
    }
  }


/*
  ServiceData buildServiceData(DatasourceLink datasourceLink) {
    return ipfsService.cat(datasourceLink.getUri(), ServiceData.class);
  }


    var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, smartContract);
    return tokenMedataOpt
        .map(TokenMetadata::getIpfsUri)
        .flatMap(this::findByIpfsLink)
        .orElseThrow(() -> new IllegalStateException(
            format("No catalog found for nftId=%d, smartContract=%s", tokenId, smartContract)
        ));
  }

  public Optional<AssetServiceDataCatalogDTO> findByIpfsLink(String tZip21Url) {
    try {
      var tokenMetadata = ipfsService.cat(tZip21Url, TZip21Metadata.class);
      return getAssetServiceDataCatalogDTO(tokenMetadata);
    } catch (Exception e) {
      log.error("While getting dataCatalog from tZip21Url={}\n{}", tZip21Url, e);
      return Optional.empty();
    }
  }

  public Optional<AssetServiceDataCatalogDTO> getAssetServiceDataCatalogDTO(TZip21Metadata tokenMetadata) {
    if (tokenMetadata == null) {
      return Optional.empty();
    }
    return tokenMetadata.getAssetDataCatalogUri()
        .map(catalogUri -> ipfsService.cat(catalogUri, AssetDataCatalog.class))
        .map(AssetDataCatalog::getDatasources)
        .map(
            links -> links.stream()
                .map(this::buildServiceData)
                .collect(Collectors.toList())
        ).map(AssetServiceDataCatalogDTO::new);
  }

  ServiceData buildServiceData(DatasourceLink datasourceLink) {
    return ipfsService.cat(datasourceLink.getUri(), ServiceData.class);
  }
*/

}
