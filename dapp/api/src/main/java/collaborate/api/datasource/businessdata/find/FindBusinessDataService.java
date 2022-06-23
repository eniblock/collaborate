package collaborate.api.datasource.businessdata.find;

import collaborate.api.comparator.SortComparison;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Optional;
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
  private final SortComparison sortComparison;

  public Page<AssetDetailsDTO> find(Pageable pageable, Optional<String> query,
      Optional<String> ownerAddress) {
    var dspWallets = ownerAddress.map(List::of)
        .orElse(organizationService.getAllDspWallets());

    var assetByDsp = findBusinessDataDAO.findNftIndexersByDsps(dspWallets);

    var filteredAssetDetails = assetByDsp.streamTokenIndexes()
        .filter(tokenIndex ->
            query.map(q -> tokenIndex.getAssetId().contains(q))
                .orElse(true)
        )
        .map(assetDetailsService::toAssetDetails).collect(Collectors.toList());

    var assetDetails = sortComparison.sorted(
            filteredAssetDetails.stream(), pageable.getSort(), AssetDetailsDTO.class)
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .collect(Collectors.toList());
    return new PageImpl<>(assetDetails, pageable, filteredAssetDetails.size());
  }


}
