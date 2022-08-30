package collaborate.api.datasource.businessdata;


import static java.util.stream.Collectors.toMap;

import collaborate.api.datasource.businessdata.find.BusinessDataNftIndexerService;
import collaborate.api.datasource.create.MintBusinessDataJsonNodeParams;
import collaborate.api.datasource.model.Nft;
import collaborate.api.datasource.model.assetId;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.tag.model.Bytes;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NftService {

  public static final String OWNER_FILTER_KEY = "owner";
  private final BusinessDataNftIndexerService businessDataNftIndexerService;
  private final OrganizationService organizationService;
  private final NftRepository nftRepository;
  private final ObjectMapper objectMapper;

  public Optional<Nft> findById(String datasourceId, String alias) {
    return nftRepository
        .findById(new assetId(datasourceId, alias));
  }

  public void updateNft(Transaction transaction) {
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

    var nftScope = nftRepository.findById(new assetId(assetId));
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
              .assetId(new assetId(assetId))
              .nftId(indexedNft.getTokenId())
              .ownerAddress(transaction.getSource())
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

  public Page<Nft> findMarketPlaceByFilters(Map<String, String> filters, Pageable pageable) {
    var nftSpec = new NftSpecification(null);
    if (filters != null && filters.containsKey(OWNER_FILTER_KEY)) {
      var orgAddress = organizationService
          .findByLegalNameIgnoreCase(filters.get(OWNER_FILTER_KEY))
          .map(OrganizationDTO::getAddress);
      if (orgAddress.isEmpty()) {
        return Page.empty();
      }
      nftSpec.setEqOwnerAddress(orgAddress.get());
    } else {
      nftSpec.setNotEqOwnerAddress(organizationService.getCurrentAddress());
    }
    if (filters != null) {
      var metadataFilters = filters.entrySet().stream()
          .filter(entry -> !entry.getKey().equals(OWNER_FILTER_KEY))
          .collect(toMap(Entry::getKey, Entry::getValue));
      nftSpec.setMetadata(metadataFilters);
    }
    return nftRepository.findAll(nftSpec, pageable);
  }

}
