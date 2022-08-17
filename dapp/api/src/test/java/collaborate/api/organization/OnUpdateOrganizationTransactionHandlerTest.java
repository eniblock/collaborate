package collaborate.api.organization;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.organization.model.UpdateOrganizationTypeDTO;
import collaborate.api.organization.tag.Organization;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnUpdateOrganizationTransactionHandlerTest {

  @InjectMocks
  OnUpdateOrganizationTransactionHandler onUpdateOrganizationTransactionHandler;

  @Mock
  OrganizationService organizationService;
  @Mock
  PendingOrganizationService pendingOrganizationService;

  @BeforeEach
  void setUp() {
    onUpdateOrganizationTransactionHandler = new OnUpdateOrganizationTransactionHandler(
        TestResources.objectMapper,
        organizationService,
        pendingOrganizationService
    );
  }

  @Test
  void toUpdateTransactionTypeDTOs_shouldDeserialized_aRemoveOperation() {
    // GIVEN
    JsonNode removeTransactionParams = TestResources.readFileAsJsonNode(
        "/organizations/update_organization.entry_point.parameter.remove.json");
    // WHEN
    var updateOrRemoveResults = onUpdateOrganizationTransactionHandler.toUpdateTransactionTypeDTOs(
        removeTransactionParams);
    // THEN
    assertThat(updateOrRemoveResults).containsExactlyInAnyOrder(
        UpdateOrganizationTypeDTO.builder()
            .remove("tz1g53cuGXWjy6uPPzc4N3MTwifjRmydX5rD")
            .build()
    );
  }

  @Test
  void toUpdateTransactionTypeDTOs_shouldDeserialized_anUpdateOperation() {
    // GIVEN
    JsonNode updateTransactionParams = TestResources.readFileAsJsonNode(
        "/organizations/update_organization.entry_point.parameter.update.json");
    // WHEN
    var updateOrRemoveResults = onUpdateOrganizationTransactionHandler.toUpdateTransactionTypeDTOs(
        updateTransactionParams);
    // THEN
    assertThat(updateOrRemoveResults).containsExactlyInAnyOrder(
        UpdateOrganizationTypeDTO.builder()
            .update(Organization.builder()
                .roles(List.of(OrganizationRole.BNO, OrganizationRole.DSP))
                .address("tz1d9eoHqwhXsgndszk7zyqSJwZh9HAFmbxF")
                .legalName("SITA")
                .encryptionKey("encryptionA")
                .build()
            )
            .build()
    );

  }

}
