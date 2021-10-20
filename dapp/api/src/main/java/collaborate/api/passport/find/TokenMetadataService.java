package collaborate.api.passport.find;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.ipfs.domain.dto.ContentWithCid;
import collaborate.api.passport.create.DatasourceLink;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.passport.model.metadata.AssetDataCatalog;
import collaborate.api.passport.model.metadata.TokenMetadata;
import collaborate.api.passport.model.storage.Multisig;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenMetadataService {

  private final IpfsService ipfsService;
  private final DatasourceService datasourceService;

  public Optional<AssetDataCatalogDTO> findDataCatalog(Multisig multisig,
      Integer multisigContractId) {
    try {
      if (multisig.getParam2() == null || StringUtils.isEmpty(multisig.getParam2().toString())) {
        log.warn("No token metadata found for multisigContractId={}", multisigContractId);
        return Optional.empty();
      } else {
        return findDataCatalog(multisig.getParam2().toString());
      }
    } catch (Exception e) {
      log.error("While getting dataCatalog from multisigContractId={}\n{}", multisigContractId, e);
      return Optional.empty();
    }
  }

  public Optional<AssetDataCatalogDTO> findDataCatalog(String metadataIpfsLink) {
    try {
      var tokenMetadata = ipfsService.cat(metadataIpfsLink, TokenMetadata.class);
      return tokenMetadata.getAssetDataCatalogUri()
          .map(catalogUri -> ipfsService.cat(catalogUri, AssetDataCatalog.class))
          .map(AssetDataCatalog::getDatasources)
          .map(this::buildDatasourceDTO)
          .map(AssetDataCatalogDTO::new);
    } catch (Exception e) {
      log.error("While getting dataCatalog from metadataIpfsLink={}\n{}", metadataIpfsLink, e);
      return Optional.empty();
    }
  }

  public List<DatasourceDTO> buildDatasourceDTO(List<DatasourceLink> datasourceLinks) {
    return datasourceLinks.stream().map(d ->
        DatasourceDTO.builder()
            .id(d.getId())
            .assetIdForDatasource(d.getAssetIdForDatasource())
            .baseUri(datasourceService.findById(d.getId())
                .map(ContentWithCid::getContent)
                .map(datasourceService::buildDatasourceBaseUri)
                .orElse("")
            ).scopes(datasourceService.getScopesByDataSourceId(d.getId())
            .orElseGet(() -> {
              log.warn("No scopes found for datasource={}", d.getId());
              return Collections.emptySet();
            })
        ).build()
    ).collect(Collectors.toList());
  }

}
