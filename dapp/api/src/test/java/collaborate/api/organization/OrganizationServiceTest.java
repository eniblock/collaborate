package collaborate.api.organization;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.UserService;
import java.util.List;
import java.util.Optional;
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
  ApiProperties apiProperties;
  @Mock
  OrganizationDAO organizationDAO;
  @Mock
  UserService userService;
  @InjectMocks
  OrganizationService organizationService;

  @ParameterizedTest
  @MethodSource("extractValuePathParameters")
  void getAllDspWallets_shouldIncludeDspRoleAndExcludeDspRole(
      List<OrganizationDTO> organizations, List<String> expectedWalletsResult) {
    // GIVEN
    when(organizationDAO.getAllOrganizations())
        .thenReturn(organizations);
    when(userService.getAdminUser())
        .thenReturn(UserWalletDTO.builder()
            .address("adminWallet")
            .userId("admin")
            .build());
    when(organizationDAO.findOrganizationByPublicKeyHash("adminWallet"))
        .thenReturn(Optional.of(
            OrganizationDTO.builder()
                .roles(List.of(OrganizationRole.BSP))
                .build()));
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

  @Test
  void findByLegalNameIgnoreCase() {
    // GIVEN
    when(userService.getAdminUser())
        .thenReturn(UserWalletDTO.builder()
            .address("address")
            .build()
        );
    when(organizationDAO.getAllOrganizations())
        .thenReturn(List.of(OrganizationDTO.builder()
                .legalName("Organization")
                .build()
            )
        );
    // WHEN
    var organizationResult = organizationService.findByLegalNameIgnoreCase("organization");
    // THEN
    assertThat(organizationResult).isNotEmpty();
  }
}
