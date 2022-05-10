package collaborate.api.ipfs;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IpfsDAOTest {

  @Mock
  private IpfsClient ipfsClient;
  @Spy
  private ObjectMapper objectMapper = TestResources.objectMapper;

  @InjectMocks
  private IpfsDAO ipfsDAO;


  @Test
  void cat_shouldReturnExpected_withJsonNodeClass() {
    // GIVEN
    String cid = "cid";
    when(ipfsClient.cat(cid))
        .thenReturn(readContent("/ipfs/ipfs.cat.tzip21.json"));
    // WHEN
    var jsonNodeResult = ipfsDAO.cat(cid, JsonNode.class);
    // THEN
    assertThat(jsonNodeResult).isNotEmpty();
    assertThat(jsonNodeResult.get("name").asText()).isEqualTo("CatalogBusinessData");
    assertThat(jsonNodeResult.get("attributes")).hasSize(2);
  }
}
