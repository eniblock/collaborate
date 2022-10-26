package collaborate.api.datasource.servicedata.find;

import collaborate.api.datasource.businessdata.TAGBusinessDataClient;
import collaborate.api.datasource.businessdata.find.IndexerTagResponseDTO;
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
  private final TAGBusinessDataClient tagClient;

  public IndexerTagResponseDTO findNftIndexersByDsps(
      Collection<String> dspAddresses) {
    var requestPassportsIndexer = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.NFT_INDEXER, dspAddresses)
    ));
    return tagClient
        .getIndexer(
            serviceDataContractAddress,
            requestPassportsIndexer
        );
  }

}
