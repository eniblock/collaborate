package collaborate.api.businessdata.access.request;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.businessdata.access.request.model.AccessRequestParams;
import collaborate.api.nft.model.AssetDetailsDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessRequestDAOTest {

  @InjectMocks
  AccessRequestDAO accessRequestDAO;

  AssetDetailsDTO assetDetails = readContent(
      "/businessdata/access/request/asset-details-with-multiple-datasources.json",
      AssetDetailsDTO.class);

  @Test
  void toAccessRequestParam() {
    // GIVEN
    // WHEN
    String requesterWallet = "requester";
    var accessRequests =
        assetDetails.getAssetDataCatalog().getDatasources().stream()
            .map(d -> accessRequestDAO.toAccessRequestParam(17, d,
                assetDetails.getAssetOwner().getAddress()));
    // THEN
    assertThat(accessRequests).containsExactlyInAnyOrderElementsOf(
        List.of(
            AccessRequestParams.builder()
                .nftId(17)
                .scopes(List.of("29dba35d-80b5-4e48-ad8b-602b01be843c:customers-analytics-a"))
                .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                .build(),
            AccessRequestParams.builder()
                .nftId(17)
                .scopes(List.of("1288d1f4-3674-4efb-8ec0-c5e6a4bcffd7:customers-analytics-b"))
                .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                .build()
        )
    );
  }
}
