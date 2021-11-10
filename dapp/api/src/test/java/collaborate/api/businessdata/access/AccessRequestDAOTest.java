package collaborate.api.businessdata.access;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.businessdata.access.model.AccessRequestParams;
import collaborate.api.config.UUIDGenerator;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.test.UUIDTestGenerator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessRequestDAOTest {

  @Mock
  UUIDGenerator uuidGenerator;

  @InjectMocks
  AccessRequestDAO accessRequestDAO;

  AssetDetailsDTO assetDetails = readContent(
      "/businessdata/access/request/asset-details-with-multiple-datasources.json",
      AssetDetailsDTO.class);

  @Test
  void toAccessRequestParam() {
    // GIVEN
    var uuidTestGenerator = new UUIDTestGenerator();
    when(uuidGenerator.randomUUID()).thenAnswer((invocation) -> uuidTestGenerator.next());
    // WHEN
    var accessRequests =
        assetDetails.getAssetDataCatalog().getDatasources().stream()
            .map(d -> accessRequestDAO.toAccessRequestParam(17, d,
                assetDetails.getAssetOwner().getAddress()))
            .collect(Collectors.toList());
    // THEN
    assertThat(accessRequests).containsExactlyInAnyOrderElementsOf(
        List.of(
            AccessRequestParams.builder()
                .accessRequestsUuid(uuidTestGenerator.get(0))
                .nftId(17)
                .scopes(List.of("29dba35d-80b5-4e48-ad8b-602b01be843c:customers-analytics-a"))
                .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                .build(),
            AccessRequestParams.builder()
                .accessRequestsUuid(uuidTestGenerator.get(1))
                .nftId(17)
                .scopes(List.of("1288d1f4-3674-4efb-8ec0-c5e6a4bcffd7:customers-analytics-b"))
                .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                .build()
        )
    );
  }
}
