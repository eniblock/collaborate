package collaborate.api.datasource.servicedata.find;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import collaborate.api.comparator.SortComparison;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.servicedata.nft.ServiceDataNftService;
//import collaborate.api.datasource.servicedata.transaction.ServiceDataTransactionService;
import collaborate.api.datasource.businessdata.find.BusinessDataNftIndexerService;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.kpi.KpiSpecification;
import collaborate.api.datasource.model.Nft;
import collaborate.api.datasource.model.AssetId;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.servicedata.model.ServiceDataAssetDetailsDTO;
import collaborate.api.datasource.servicedata.model.ServiceDataDTOElement;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.datasource.servicedata.model.ServiceData;
import collaborate.api.datasource.nft.TokenDAO;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.tag.model.TokenMetadata;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collections;
import java.util.Arrays;
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
  private final BusinessDataNftIndexerService businessDataNftIndexerService;
  private final String serviceDataContractAddress;
  //private final ServiceDataTransactionService serviceDataTransactionService;
  private final ServiceDataNftService nftService;
  private final OrganizationService organizationService;
  private final KpiService kpiService;
  private final SortComparison sortComparison;
  private final IpfsService ipfsService;
  private final TokenDAO tokenMetadataDAO;

  public Page<ServiceDataAssetDetailsDTO> marketPlace(Map<String, String> filters, Pageable pageable) {
    return nftService.findMarketPlaceByFilters(filters, pageable)
        .map(this::toAssetDetails);
  }

  ServiceDataAssetDetailsDTO toAssetDetails(Nft t) {
    var datasourceId = t.getDatasourceId();
    var alias = t.getAssetId().getAlias();
    //var creationDate = serviceDataTransactionService.findTransactionDateByTokenId(serviceDataContractAddress, t.getAssetId().toString());
    
    String name = "", date = "";
    try {
      Integer tokenId = t.getNftId();
      var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, serviceDataContractAddress);
      name = tokenMedataOpt.get().getTokenInfo().stream()
        .filter(tagEntry -> "name".equals(tagEntry.getKey()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Can't find metadata field for tokenId=" + tokenId)
        ).getValue()
        .toString();
      date = tokenMedataOpt.get().getTokenInfo().stream()
        .filter(tagEntry -> "date".equals(tagEntry.getKey()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Can't find metadata field for tokenId=" + tokenId)
        ).getValue()
        .toString();
    } catch (Exception e) {
      //log.error("Error getting name in metadata: {}", e);
    }

    return ServiceDataAssetDetailsDTO.builder()
        //.accessStatus(getAccessStatus(datasourceId, t.getNftId()))
        /*
        .services(
          List.of(alias.split("_"))
          .stream()
          .map(s -> 
            ServiceDataDTOElement.builder()
              .datasource(s.split("=")[0])
              .scope(s.split("=").length > 1 ? s.split("=")[1] : new String())
              .build()
          )
          .collect(Collectors.toList())
        )
        */
        .id(t.getAssetId().toString().split(":")[0])
        .name(name)
        .creationDatetime(date)
        .assetOwner(Optional.ofNullable(t.getOwnerAddress())
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
    Optional<Nft> t = nftService.findById(assetId+":service-data");
    if (t.isPresent()) {
        Integer tokenId = t.get().getNftId();
        if (tokenId != null) {
          var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, serviceDataContractAddress);
          
          var serviceData = tokenMedataOpt
            .map(TokenMetadata::getIpfsUri)
            .flatMap(this::findByIpfsLink)
            .orElseThrow(() -> new IllegalStateException(
                format("No metadata found for nftId=%d, smartContract=%s", tokenId, serviceDataContractAddress)
            ));

          var services = serviceData.getServices()
            .stream()
            .map(s -> {
              var indexedNft = businessDataNftIndexerService.find(
                  Optional.of(tokenIndex -> tokenIndex.getAssetId().contains(s.getValue())),
                  Optional.of(serviceData.getOwner())
                ).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("nft not found for assetId =" + s.getValue()));              
              return Metadata.builder()
              .name(indexedNft.getTokenId().toString())
              .value(s.getValue())
              .type("business-data")
              .build();
            })
            .collect(Collectors.toSet());

          return ServiceData.builder()
          .id(tokenId.toString()) //t.get().getAssetId().toString())
          .name(serviceData.getName())
          .description(serviceData.getDescription())
          .creationDatetime(serviceData.getCreationDatetime())
          .owner(serviceData.getOwner())
          .services(services)
          .build();
        }
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
}
