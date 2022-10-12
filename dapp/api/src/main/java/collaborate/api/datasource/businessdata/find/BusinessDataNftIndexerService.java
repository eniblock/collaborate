package collaborate.api.datasource.businessdata.find;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BusinessDataNftIndexerService {

  private final BusinessDataNftIndexerDAO businessDataNftIndexerDAO;
  private final OrganizationService organizationService;

  public List<TokenIndex> find(Optional<Predicate<TokenIndex>> tokenPredicate,
      Optional<String> ownerAddress) {
    var dspWallets = ownerAddress.map(List::of)
        .orElseGet(organizationService::getAllDspWallets);

    var assetByDsp = businessDataNftIndexerDAO.findNftIndexersByDsps(dspWallets);

    return assetByDsp.streamTokenIndexes()
        .filter(tokenIndex ->
            tokenPredicate.map(f -> f.test(tokenIndex))
                .orElse(true)
        ).collect(Collectors.toList());


  }

}
