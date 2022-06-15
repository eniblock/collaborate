package collaborate.api.datasource.businessdata.find;

import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindBusinessDataService {


  private final AssetDetailsService assetDetailsService;
  private final FindBusinessDataDAO findBusinessDataDAO;
  private final OrganizationService organizationService;


  public Page<AssetDetailsDTO> find(Pageable pageable, Optional<String> query,
      Optional<String> ownerAddress) {
    var dspWallets = ownerAddress.map(List::of)
        .orElse(organizationService.getAllDspWallets());

    var assetByDsp = findBusinessDataDAO.findNftIndexersByDsps(dspWallets);

    var assetDetails = assetByDsp.streamTokenIndexes()
        .filter(tokenIndex ->
            query.map(q -> tokenIndex.getAssetId().contains(q))
                .orElse(true)
        ).skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .map(assetDetailsService::toAssetDetails)
        .sorted(Comparator.comparingInt(AssetDetailsDTO::getTokenId))
        .collect(Collectors.toList());
    return new PageImpl<>(assetDetails, pageable, assetByDsp.streamTokenIndexes().count());
  }


}
