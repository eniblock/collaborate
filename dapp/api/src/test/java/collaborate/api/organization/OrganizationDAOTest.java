package collaborate.api.organization;

import static collaborate.api.organization.OrganizationFeature.bspConsortium2Organization;
import static collaborate.api.organization.OrganizationFeature.dspConsortium1Organization;
import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.organization.tag.Organization;
import collaborate.api.organization.tag.OrganizationMap;
import collaborate.api.organization.tag.TezosApiGatewayOrganizationClient;
import collaborate.api.organization.tag.UpdateOrganisationFactory;
import collaborate.api.tag.TezosApiGatewayJobClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class OrganizationDAOTest {

  private OrganizationDAO organizationDAO;

  @Mock
  private PendingOrganizationRepository pendingOrganizationRepository;
  @Mock
  private TezosApiGatewayOrganizationClient tezosApiGatewayOrganizationClient;
  @Mock
  private SmartContractAddressProperties smartContractAddressProperties;

  @Mock
  private TezosApiGatewayJobClient tezosApiGatewayJobClient;

  private final UpdateOrganisationFactory updateOrganisationFactory = new UpdateOrganisationFactory();

  @BeforeEach
  void setUp() {
    organizationDAO = new OrganizationDAO(
        new ModelMapper(),
        pendingOrganizationRepository,
        smartContractAddressProperties,
        tezosApiGatewayOrganizationClient,
        tezosApiGatewayJobClient,
        updateOrganisationFactory
    );
  }

  @Test
  void getAllOrganizations_shouldReturnExpected() throws IOException {
    // GIVEN
    String organizationJson = OrganizationFeature.organizationTagResponseJson;
    var indexerResponse = objectMapper.readValue(organizationJson, OrganizationMap.class);

    when(smartContractAddressProperties.getOrganizationYellowPage())
        .thenReturn("yellowPageAddress");
    when(tezosApiGatewayOrganizationClient
        .getOrganizations("yellowPageAddress", OrganizationDAO.GET_ALL_ORGANIZATIONS_REQUEST))
        .thenReturn(indexerResponse);
    // WHEN
    var actualOrganizations = organizationDAO.getAllOrganizations();
    // THEN
    assertThat(actualOrganizations).containsExactlyInAnyOrder(
        dspConsortium1Organization,
        bspConsortium2Organization
    );
  }

  @Test
  void getAllOrganizations_shouldSerializeRequestAsExpected() throws JsonProcessingException {
    // GIVEN
    // WHEN
    var serialization = objectMapper
        .writeValueAsString(OrganizationDAO.GET_ALL_ORGANIZATIONS_REQUEST);
    // THEN
    assertThat(serialization).isEqualTo("{\"dataFields\":[\"organizations\"]}");
  }

  @Test
  void toOrganization_shouldReturnExpectedMapping(){
    // GIVEN
    var organizationDTO = OrganizationFeature.validOrganization;
    var expectedOrganization = Organization.builder()
        .encryptionKey(organizationDTO.getEncryptionKey())
        .roles(organizationDTO.getRoles())
        .address(organizationDTO.getAddress())
        .legalName(organizationDTO.getLegalName())
        .build();
    // WHEN
    var actualOrganization = organizationDAO.toOrganization(organizationDTO);
    // THEN
    assertThat(actualOrganization).isEqualTo(expectedOrganization);
  }
}
