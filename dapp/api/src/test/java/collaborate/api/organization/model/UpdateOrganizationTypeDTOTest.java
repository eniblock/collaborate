package collaborate.api.organization.model;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.organization.tag.Organization;
import org.junit.jupiter.api.Test;

public class UpdateOrganizationTypeDTOTest {

  @Test
  void getAddress_shouldReturnOrganizationAddress_withUpdate() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .update(Organization.builder().address("any").build())
        .build();
    // WHEN
    var actualAddress = updateOrganizationTypeDTO.getAddress();
    // THEN
    assertThat(actualAddress).isEqualTo("any");
  }

  @Test
  void getAddress_shouldReturnRemoveAddress_withRemove() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .remove("any"
            + "")
        .build();
    // WHEN
    var actualAddress = updateOrganizationTypeDTO.getAddress();
    // THEN
    assertThat(actualAddress).isEqualTo("any");
  }

  @Test
  void isUpdate_shouldBeTrue_withUpdateFieldValued() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .update(new Organization())
        .build();
    // WHEN
    var actualIsUpdate = updateOrganizationTypeDTO.isUpdateType();
    // THEN
    assertThat(actualIsUpdate).isTrue();
  }

  @Test
  void isUpdate_shouldBeFalse_withRemoveFieldValued() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .remove("any")
        .build();
    // WHEN
    var actualIsUpdate = updateOrganizationTypeDTO.isUpdateType();
    // THEN
    assertThat(actualIsUpdate).isFalse();
  }


  @Test
  void isUpdate_shouldBeFalse_withBothUpdateAndRemoveFieldsValued() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .update(new Organization())
        .remove("any")
        .build();
    // WHEN
    var actualIsUpdate = updateOrganizationTypeDTO.isUpdateType();
    // THEN
    assertThat(actualIsUpdate).isFalse();
  }


  @Test
  void isRemove_shouldBeTrue_withRemoveFieldValued() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .remove("any")
        .build();
    // WHEN
    var actualIsUpdate = updateOrganizationTypeDTO.isRemoveType();
    // THEN
    assertThat(actualIsUpdate).isTrue();
  }

  @Test
  void isRemove_shouldBeFalse_withUpdateFieldValued() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .update(new Organization())
        .build();
    // WHEN
    var actualIsRemove = updateOrganizationTypeDTO.isRemoveType();
    // THEN
    assertThat(actualIsRemove).isFalse();
  }

  @Test
  void isRemove_shouldBeFalse_withBothUpdateAndRemoveFieldsValued() {
    // GIVEN
    var updateOrganizationTypeDTO = UpdateOrganizationTypeDTO.builder()
        .update(new Organization())
        .remove("any")
        .build();
    // WHEN
    var actualIsRemove = updateOrganizationTypeDTO.isRemoveType();
    // THEN
    assertThat(actualIsRemove).isFalse();
  }
}
