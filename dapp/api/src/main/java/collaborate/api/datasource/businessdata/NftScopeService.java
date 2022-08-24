package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.businessdata.find.BusinessDataNftIndexerService;
import collaborate.api.datasource.create.MintBusinessDataParamsDTO;
import collaborate.api.datasource.model.NFTScopeId;
import collaborate.api.datasource.model.NftScope;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NftScopeService {

  private final BusinessDataNftIndexerService businessDataNftIndexerService;
  private final NftScopeRepository nftScopeRepository;
  private final ObjectMapper objectMapper;

  public Optional<NftScope> findById(String datasourceId, String alias) {
    return nftScopeRepository
        .findById(new NFTScopeId(datasourceId, alias));
  }

  public Optional<String> findScopeById(String datasourceId, String alias) {
    return findById(datasourceId, alias)
        .map(NftScope::getScope);
  }

  public void updateNftId(Transaction transaction, String currentOrganizationAddress) {
    String assetId;
    try {
      assetId = objectMapper.treeToValue(
          transaction.getParameters(),
          MintBusinessDataParamsDTO.class
      ).getAssetId();
    } catch (JsonProcessingException e) {
      log.error("While working with transaction={}", transaction);
      throw new IllegalStateException("Can't deserialize mint business-data transaction params", e);
    }
    var indexedNft = businessDataNftIndexerService.find(
            Optional.of(tokenIndex -> tokenIndex.getAssetId().equals(assetId)),
            Optional.of(currentOrganizationAddress)
        ).stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("nft not found for assetId =" + assetId));

    var nftScope = nftScopeRepository.findById(new NFTScopeId(assetId));
    if (nftScope.isPresent()) {
      nftScope.get().setNftId(indexedNft.getTokenId());
    } else {
      nftScope = Optional.of(new NftScope(new NFTScopeId(assetId), null, indexedNft.getTokenId()));
    }
    nftScopeRepository.save(nftScope.get());
  }

  public NftScope save(NftScope nftScope) {
    return nftScopeRepository.save(nftScope);
  }

  public Optional<NftScope> findOneByNftId(Integer nftId) {
    return nftScopeRepository.findOneByNftId(nftId);
  }
}
