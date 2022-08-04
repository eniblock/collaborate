package collaborate.api.datasource.businessdata.find;

import collaborate.api.comparator.SortComparison;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
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
  private final SortComparison sortComparison;

  public Page<AssetDetailsDTO> find(Pageable pageable, Optional<Predicate<TokenIndex>> filter,
      Optional<String> ownerAddress) {
    var dspWallets = ownerAddress.map(List::of)
        .orElseGet(organizationService::getAllDspWallets);

    var assetByDsp = findBusinessDataDAO.findNftIndexersByDsps(dspWallets);

    var filteredAssetDetails = assetByDsp.streamTokenIndexes()
        .filter(tokenIndex ->
            filter.map(f -> f.test(tokenIndex))
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

  public Page<AssetDetailsDTO> marketPlace(Pageable pageable) {
    var ownerAddress = organizationService.getCurrentOrganization().getAddress();
    Predicate<TokenIndex> predicate = t -> !t.getTokenOwnerAddress().equals(ownerAddress);
    return find(pageable, Optional.of(predicate), Optional.empty());
  }
}
