package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.businessdata.find.BusinessDataNftIndexerService;
import collaborate.api.datasource.create.MintBusinessDataJsonNodeParams;
import collaborate.api.datasource.model.NFTScopeId;
import collaborate.api.datasource.model.Nft;
import collaborate.api.tag.model.Bytes;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NftScopeService {

  private final BusinessDataNftIndexerService businessDataNftIndexerService;
  private final NftRepository nftRepository;
  private final ObjectMapper objectMapper;

  public Optional<Nft> findById(String datasourceId, String alias) {
    return nftRepository
        .findById(new NFTScopeId(datasourceId, alias));
  }

  public void updateNftId(Transaction transaction) {
    MintBusinessDataJsonNodeParams mintBusinessDataParams;
    try {
      mintBusinessDataParams = objectMapper.treeToValue(
          transaction.getParameters(),
          MintBusinessDataJsonNodeParams.class
      );
    } catch (JsonProcessingException e) {
      log.error("While working with transaction={}", transaction);
      throw new IllegalStateException("Can't deserialize mint business-data transaction params", e);
    }
    var assetId = mintBusinessDataParams.getAssetId();
    var indexedNft = businessDataNftIndexerService.find(
            Optional.of(tokenIndex -> tokenIndex.getAssetId().equals(assetId)),
            Optional.of(transaction.getSource())
        ).stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("nft not found for assetId =" + assetId));

    var nftScope = nftRepository.findById(new NFTScopeId(assetId));
    if (nftScope.isPresent()) {
      // Minted by current organization
      nftScope.get().setNftId(indexedNft.getTokenId());
    } else {
      var metadataNode = (ObjectNode) mintBusinessDataParams.getMetadata();
      mintBusinessDataParams.getMetadata().fields()
          .forEachRemaining(entry -> metadataNode.put(
              entry.getKey(),
              new Bytes(entry.getValue().asText()).toString()
          ));
      // Minted by another organization
      nftScope = Optional.of(
          Nft.builder()
              .nftScopeId(new NFTScopeId(assetId))
              .nftId(indexedNft.getTokenId())
              .metadata(mintBusinessDataParams.getMetadata())
              .build());
    }
    nftRepository.save(nftScope.get());
  }

  public Nft save(Nft nft) {
    return nftRepository.save(nft);
  }

  public Optional<Nft> findOneByNftId(Integer nftId) {
    return nftRepository.findOneByNftId(nftId);
  }
}
