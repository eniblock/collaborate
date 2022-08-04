package collaborate.api.organization.model;

import static collaborate.api.test.TestResources.objectMapper;
import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class OrganizationDTOTest {

  @Test
  void deserialize() {
    // GIVEN

    // WHEN
    var organization = readContent("/organizations/organization.json", OrganizationDTO.class);

    // THEN
    assertThat(organization)
        .isEqualTo(
            OrganizationDTO.builder()
                .address("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                .legalName("dsp")
                .roles(List.of(OrganizationRole.BSP))
                .encryptionKey("edpkv2qoSugVizsZRt9dCb2v4iizRhZEQkw2PF5JyZUgHyE6Bp9Yv2")
                .status(OrganizationStatus.ACTIVE)
                .build()
        );
  }

  @ParameterizedTest
  @EnumSource(OrganizationRole.class)
  void serializeThenDeserialize_shouldBeIdentity(OrganizationRole role)
      throws JsonProcessingException {
    // GIVEN

    var organization = OrganizationDTO.builder()
        .address("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
        .legalName("dsp")
        .roles(List.of(role))
        .encryptionKey("edpkv2qoSugVizsZRt9dCb2v4iizRhZEQkw2PF5JyZUgHyE6Bp9Yv2")
        .status(OrganizationStatus.ACTIVE)
        .build();
    // WHEN
    var organizationResult = objectMapper.readValue(
        objectMapper.writeValueAsString(organization),
        OrganizationDTO.class
    );
    // THEN
    assertThat(organization)
        .isEqualTo(organizationResult);
  }
}
