package collaborate.api.organization;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.model.OrganizationRole;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

  @Mock
  OrganizationDAO organizationDAO;
  @Mock
  ApiProperties apiProperties;
  @InjectMocks
  OrganizationService organizationService;

  final String businessDataSCAddress = "bdSCAddress";
  final String digitalPassportDataSCAddress = "dpSCAddress";

  @ParameterizedTest
  @MethodSource("extractValuePathParameters")
  void getAllDspWallets_shouldIncludeDspRoleAndExcludeDspRole(
      List<OrganizationDTO> organizations, List<String> expectedWalletsResult) {
    // GIVEN
    when(apiProperties.getBusinessDataContractAddress()).thenReturn(businessDataSCAddress);
    when(organizationDAO.getAllOrganizations(any()))
        .thenReturn(organizations);
    // WHEN
    var dspWalletsResult = organizationService.getAllDspWallets();
    // THEN
    assertThat(dspWalletsResult).containsExactlyInAnyOrderElementsOf(expectedWalletsResult);
  }

  private static Stream<Arguments> extractValuePathParameters() {
    return Stream.of(
        // Should exclude BSP
        Arguments.of(
            List.of(
                OrganizationDTO.builder()
                    .address("orgA")
                    .roles(List.of(OrganizationRole.BSP))
                    .build()
            ),
            emptyList()
        ),
        // Should include DSP
        Arguments.of(
            List.of(
                OrganizationDTO.builder()
                    .address("orgB")
                    .roles(List.of(OrganizationRole.DSP))
                    .build()
            ),
            List.of("orgB")
        ),
        // Should ignore organization without role
        Arguments.of(
            List.of(
                OrganizationDTO.builder()
                    .address("orgA")
                    .roles(emptyList())
                    .build()
            ),
            emptyList()
        )
    );
  }
}
