package collaborate.api.organization.tag;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

public class OrganisationTest {

  @Test
  void deserialize_handleSnakeCase() throws JsonProcessingException {
    // GIVEN
    String json = "{\"legal_name\":\"DSPConsortium1\"}";
    // WHEN
    var organization = objectMapper.readValue(json, Organization.class);
    // THEN
    assertThat(organization).isEqualTo(Organization.builder().legalName("DSPConsortium1").build());
  }
}
