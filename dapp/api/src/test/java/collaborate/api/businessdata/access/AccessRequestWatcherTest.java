package collaborate.api.businessdata.access;


import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.transaction.Transaction;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessRequestWatcherTest {

  public static final String PROVIDER_ADDRESS = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV";
  @Mock
  OrganizationService organizationService;
  @InjectMocks
  AccessRequestWatcher accessRequestWatcher;

  @ParameterizedTest
  @MethodSource("isRequestAccessForCurrentOrganisationParams")
  void isRequestAccessForCurrentOrganisation(
      String entryPoint, String provider, boolean expectedResult) {
    // GIVEN
    when(organizationService.getCurrentOrganization())
        .thenReturn(
            OrganizationDTO.builder()
                .address(PROVIDER_ADDRESS)
                .build()
        );
    accessRequestWatcher.init();
    var transaction = Transaction.builder()
        .entrypoint(entryPoint)
        .parameters(objectMapper.createObjectNode()
            .put("provider_address", provider))
        .build();
    // WHEN
    var currentResult = accessRequestWatcher.isRequestAccessForCurrentOrganisation(transaction);
    // THEN
    assertThat(currentResult).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> isRequestAccessForCurrentOrganisationParams() {
    return Stream.of(
        Arguments.of("request_access", PROVIDER_ADDRESS, true),
        Arguments.of("request_access", "wrongProvider", false),
        Arguments.of("wrongEntrypoint", PROVIDER_ADDRESS, false),
        Arguments.of("wrongEntrypoint", "wrongProvider", false)
    );
  }
}
