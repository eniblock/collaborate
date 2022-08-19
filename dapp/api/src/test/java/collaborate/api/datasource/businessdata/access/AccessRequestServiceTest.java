package collaborate.api.datasource.businessdata.access;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessRequestServiceTest {

  @InjectMocks
  AccessRequestService accessRequestService;

  AccessRequestDTO assetDetails = readContent(
      "/datasource/businessdata/access/request/access-request-dto.json",
      AccessRequestDTO.class);

  @Test
  void toAccessRequestParam() {
    // GIVEN
    // WHEN
    var accessRequests = accessRequestService.toAccessRequestParam(assetDetails);
    // THEN
    assertThat(accessRequests).isEqualTo(
        AccessRequestParams.builder()
            .nftId(17)
            .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
            .build()
    );
  }
}
