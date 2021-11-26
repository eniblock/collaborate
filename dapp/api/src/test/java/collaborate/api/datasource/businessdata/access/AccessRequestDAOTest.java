package collaborate.api.datasource.businessdata.access;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.config.UUIDGenerator;
import collaborate.api.test.UUIDTestGenerator;
import java.util.List;
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

  AccessRequestDTO assetDetails = readContent(
      "/datasource/businessdata/access/request/access-request-dto.json",
      AccessRequestDTO.class);

  @Test
  void toAccessRequestParam() {
    // GIVEN
    var uuidTestGenerator = new UUIDTestGenerator();
    when(uuidGenerator.randomUUID()).thenAnswer((invocation) -> uuidTestGenerator.next());
    // WHEN
    var accessRequests = accessRequestDAO.toAccessRequestParam(assetDetails);
    // THEN
    assertThat(accessRequests).isEqualTo(
        AccessRequestParams.builder()
            .accessRequestsUuid(uuidTestGenerator.get(0))
            .nftId(17)
            .scopes(List.of("29dba35d-80b5-4e48-ad8b-602b01be843c:customers-analytics"))
            .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
            .build()
    );
  }
}
