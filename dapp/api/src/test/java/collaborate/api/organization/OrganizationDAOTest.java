package collaborate.api.organization;

import static collaborate.api.organization.OrganizationFeature.movidiaOrganization;
import static collaborate.api.organization.OrganizationFeature.psaOrganization;
import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayStorageClient;
import collaborate.api.tag.model.storage.IndexerResponse;
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
  private TezosApiGatewayStorageClient tezosApiGatewayStorageClient;

  @Mock
  private ApiProperties apiProperties;

  @BeforeEach
  void setUp() {
    organizationDAO = new OrganizationDAO(
        apiProperties,
        tezosApiGatewayStorageClient,
        new ModelMapper()
    );
  }

  @Test
  void getAllOrganizations_shouldReturnExpected() throws IOException {
    // GIVEN
    String organizationJson = OrganizationFeature.organizationTagResponseJson;
    var indexerResponse = objectMapper.readValue(organizationJson, IndexerResponse.class);

    String contractAdress = "contract-address";
    when(apiProperties.getContractAddress()).thenReturn(contractAdress);
    when(tezosApiGatewayStorageClient
        .getIndexer(contractAdress, OrganizationDAO.GET_ALL_ORGANIZATIONS_REQUEST))
        .thenReturn(indexerResponse);
    // WHEN
    var actualOrganizations = organizationDAO.getAllOrganizations();
    // THEN
    assertThat(actualOrganizations).containsExactlyInAnyOrder(
        psaOrganization,
        movidiaOrganization
    );
  }

  @Test
  void getAllOrganizations_shouldSerializeRequestAsExpected() throws JsonProcessingException {
    // GIVEN
    // WHEN
    var serialization = objectMapper.writeValueAsString(OrganizationDAO.GET_ALL_ORGANIZATIONS_REQUEST);
    // THEN
    assertThat(serialization).isEqualTo("{\"dataFields\":[\"organizations\"]}");
  }
}
