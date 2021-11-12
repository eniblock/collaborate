package collaborate.api.businessdata.find;

import collaborate.api.businessdata.TAGBusinessDataClient;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.passport.model.storage.StorageFields;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class FindBusinessDataDAO {

  private final ApiProperties apiProperties;
  private final TAGBusinessDataClient tagBusinessDataClient;

  public IndexerTagResponseDTO findPassportsIndexersByDsps(
      Collection<String> dspAddresses) {
    var requestPassportsIndexer = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.NFT_INDEXER, dspAddresses)
    ));
    return tagBusinessDataClient
        .getIndexer(
            apiProperties.getBusinessDataContractAddress(),
            requestPassportsIndexer
        );
  }

}
