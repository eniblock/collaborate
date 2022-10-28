package collaborate.api.datasource.servicedata.create;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_TEST_CONNECTION;
import static java.lang.String.format;
import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;

import collaborate.api.datasource.servicedata.model.ServiceDataDTO;
import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.servicedata.nft.ServiceDataNftService;
import collaborate.api.datasource.model.AssetId;
import collaborate.api.datasource.model.Nft;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.Attribute;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.datasource.nft.TokenMetadataProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class MintServiceDataService {

  private final ServiceDataNftService nftService;
  private final CreateServiceDataNftDAO createServiceDataNftDAO;
  private final DateFormatterFactory dateFormatterFactory;
  private final ObjectMapper objectMapper;
  private final TokenMetadataProperties tokenMetadataProperties;
  private final TZip21MetadataServiceFactory tZip21MetadataFactory;
  //private final ServiceTzip21MetadataFactory metadataFactory;
  private final Tzip21MetadataService tzip21MetadataService;
  private final UUIDGenerator uuidGenerator;
  private final IpfsDAO ipfsDAO;

  @Transactional
  public void mint(ServiceDataDTO serviceDataDTO, String cid) {

    var assetDTO = AssetDTO.builder()
        .assetRelativePath(IPFS_PROTOCOL_PREFIX + cid)
        .assetType("service-data")
        .datasourceUUID(serviceDataDTO.getId())
        .assetIdForDatasource(serviceDataDTO.getDatasource().toString()+"_"+serviceDataDTO.getScope())
        .tZip21Metadata(tZip21MetadataFactory.create(serviceDataDTO.getName(), serviceDataDTO.getDescription()))
        .onChainMetadata(new HashMap()) // TODO: fix ? transaction.ServiceDataTransactionParameter["metadata"]) >> Cannot deserialize value
        .build();

    var nft = Nft.builder()
        .assetId(new AssetId(assetDTO.getDatasourceUUID().toString(), assetDTO.getAssetIdForDatasource())) // ASSET ID = concat
        .metadata(objectMapper.valueToTree(assetDTO.getOnChainMetadata()))
        .status(TokenStatus.PENDING_CREATION)
        .build();

    nftService.save(nft);
    
    List<AssetMetadataMintDTO> assetIdAndUris = new ArrayList() {{
        add(buildAssetIdAndMetadataUri(assetDTO)); // PROBLEM ==> cid for ServiceData is not stored...
    }};
    
    createServiceDataNftDAO.mintServiceDataNFT(assetIdAndUris);
  }

  private AssetMetadataMintDTO buildAssetIdAndMetadataUri(AssetDTO assetDTO) {
 //   try {
      log.info("Minting asset={}", assetDTO);
      /*
      var assetDataCatalogPath = Path.of(
        tokenMetadataProperties.getAssetDataCatalogRootFolder(),
        assetDTO.getAssetRelativePath()
      );
      ipfsDAO.add(
          assetDataCatalogPath,
          assetDataCatalogFactory.create(assetDTO)
      );
     
      var tzip21 = metadataFactory.create(assetDTO);
      var buildPathForAssetId = Path.of(
        tokenMetadataProperties.getNftMetadataRootFolder(),
        assetDTO.getAssetType(),
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getNftMetadataPartitionDatePattern()),
        assetDTO.getAssetRelativePath()
      );
      var ipfsMetadataUri = IPFS_PROTOCOL_PREFIX + ipfsDAO.add(buildPathForAssetId, tzip21);
       */
      assetDTO.getOnChainMetadata().put("", new Bytes(assetDTO.getAssetRelativePath()));
      return new AssetMetadataMintDTO(
          assetDTO.getDatasourceUUID() + ":" + assetDTO.getAssetIdForDatasource(), // = ASSET ID
          assetDTO.getOnChainMetadata().entrySet().stream()
              .map(entry -> new TagEntry<>(entry.getKey(), entry.getValue(), null))
              .collect(Collectors.toCollection(LinkedList::new))
      );
/*
    } catch (IOException e) {
      log.error("error while minting asset={}", assetDTO);
      throw new IllegalStateException(e);
    }
*/
  }

}
