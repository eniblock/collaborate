package collaborate.api.datasource.businessdata.find;

import collaborate.api.test.TestResources;
import lombok.Data;


@Data
public class IndexerTagResponseFeature {

  public static final String indexerTagResponseJson =
      TestResources.readContent("/datasource/businessdata/indexer-response.json");

  public static final IndexerTagResponseDTO indexerTagResponse =
      TestResources.readContent("/datasource/businessdata/indexer-response.json",
          IndexerTagResponseDTO.class);

}
