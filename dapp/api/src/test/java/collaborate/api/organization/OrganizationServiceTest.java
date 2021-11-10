package collaborate.api.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.model.OrganizationDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @Test
  void getAllOrganizations_shouldNotContainsDuplicatedOrganizationAddress() {
    // GIVEN
    var expectedOrganizations = List.of(
        OrganizationDTO.builder()
            .legalName("DSPConsortium1A")
            .address("addressDSPConsortium1")
            .build(),
        OrganizationDTO.builder()
            .legalName("BSPConsortium2")
            .address("addressBSPConsortium2")
            .build()
    );
    when(apiProperties.getBusinessDataContractAddress()).thenReturn(businessDataSCAddress);
    when(organizationDAO.getAllOrganizations(businessDataSCAddress))
        .thenReturn(List.of(
            OrganizationDTO.builder()
                .legalName("DSPConsortium1B")
                .address("addressDSPConsortium1")
                .build()
        ));

    when(apiProperties.getDigitalPassportContractAddress())
        .thenReturn(digitalPassportDataSCAddress);
    when(organizationDAO.getAllOrganizations(digitalPassportDataSCAddress))
        .thenReturn(expectedOrganizations);
    // WHEN
    var organizationResult = organizationService.getAllOrganizations();
    // THEN
    assertThat(organizationResult).hasSameElementsAs(expectedOrganizations);
  }
}
