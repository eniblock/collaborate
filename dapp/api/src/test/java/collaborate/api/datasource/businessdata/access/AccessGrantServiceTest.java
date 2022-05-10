package collaborate.api.datasource.businessdata.access;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.test.TestResources;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessGrantServiceTest {

  ObjectMapper objectMapper = Mockito.spy(TestResources.objectMapper);
  @InjectMocks
  GrantAccessService accessGrantService;

  @Test
  void getAccessRequestParams_shouldResultInExpectedDeserializedAccessRequestParams() {
    // GIVEN
    var transaction = readContent(
        "/datasource/businessdata/access/request/access_request-transaction.json",
        Transaction.class
    );
    // WHEN
    var accessRequestResult = accessGrantService.getAccessRequestParams(transaction);
    // THEN
    assertThat(accessRequestResult)
        .isEqualTo(
            AccessRequestParams.builder()
                .nftId(3)
                .scopes(List.of("f40a829f-84d9-4dda-8ea1-3fe2fbe15635:customers-analytics"))
                .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                .accessRequestsUuid(UUID.fromString("05b108f5-1f4c-448b-bf97-8571a2a1e5dc"))
                .build()
        );
  }
}
