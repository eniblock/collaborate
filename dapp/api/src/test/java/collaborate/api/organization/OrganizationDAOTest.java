package collaborate.api.organization;

import static collaborate.api.organization.OrganizationFeature.bspConsortium2Organization;
import static collaborate.api.organization.OrganizationFeature.dspConsortium1Organization;
import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.organization.tag.OrganizationMap;
import collaborate.api.organization.tag.TezosApiGatewayOrganizationClient;
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
  private TezosApiGatewayOrganizationClient tezosApiGatewayOrganizationClient;

  @BeforeEach
  void setUp() {
    organizationDAO = new OrganizationDAO(
        tezosApiGatewayOrganizationClient,
        new ModelMapper()
    );
  }

  @Test
  void getAllOrganizations_shouldReturnExpected() throws IOException {
    // GIVEN
    String organizationJson = OrganizationFeature.organizationTagResponseJson;
    var indexerResponse = objectMapper.readValue(organizationJson, OrganizationMap.class);

    String contractAdress = "contract-address";
    when(tezosApiGatewayOrganizationClient
        .getOrganizations(contractAdress, OrganizationDAO.GET_ALL_ORGANIZATIONS_REQUEST))
        .thenReturn(indexerResponse);
    // WHEN
    var actualOrganizations = organizationDAO.getAllOrganizations(contractAdress);
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
}
