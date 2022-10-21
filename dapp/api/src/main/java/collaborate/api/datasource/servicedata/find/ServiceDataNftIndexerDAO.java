package collaborate.api.datasource.servicedata.find;

import collaborate.api.datasource.servicedata.TAGServiceDataClient;
import collaborate.api.datasource.passport.model.storage.StorageFields;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ServiceDataNftIndexerDAO {

  private final String serviceDataContractAddress;
  private final TAGServiceDataClient tagServiceDataClient;

  public IndexerTagResponseDTO findNftIndexersByDsps(
      Collection<String> dspAddresses) {
    var requestPassportsIndexer = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.NFT_INDEXER, dspAddresses)
    ));
    return tagServiceDataClient
        .getIndexer(
            serviceDataContractAddress,
            requestPassportsIndexer
        );
  }

}
