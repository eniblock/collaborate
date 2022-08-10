package collaborate.api.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.organization.model.UpdateOrganizationTypeDTO;
import collaborate.api.organization.tag.Organization;
import collaborate.api.user.UserService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PendingOrganizationServiceTest {

  @InjectMocks
  PendingOrganizationService pendingOrganizationService;

  @Mock
  OrganizationService organizationService;

  @Mock
  PendingOrganizationRepository pendingOrganizationRepository;

  @Mock
  UserService userService;

  UpdateOrganizationTypeDTO buildUpdateForAddress(String address) {
    return UpdateOrganizationTypeDTO.builder()
        .update(buildOrganizationForAddress(address))
        .build();
  }

  Organization buildOrganizationForAddress(String address) {
    return Organization.builder()
        .address(address)
        .build();
  }

  @Test
  void findKnownPendingAddresses_shouldReturnsEmptyWhenNoMatchingAddresses() {
    // GIVEN
    var findIds = List.of("a1", "a2", "a3");
    var updateOrganizationsTypes = findIds.stream()
        .map(this::buildUpdateForAddress)
        .collect(Collectors.toList());

    when(pendingOrganizationRepository.findAllById(findIds)).thenReturn(
        Collections.emptyList()
    );
    // WHEN
    var actualKnown = pendingOrganizationService.findKnownPendingAddresses(
        updateOrganizationsTypes);
    // THEN
    assertThat(actualKnown).isEmpty();
  }

  @Test
  void findKnownPendingAddresses_shouldReturnsOnlyMatchingAddresses() {
    // GIVEN
    var findIds = List.of("a1", "a2", "a3");
    var updateOrganizationsTypes = findIds.stream()
        .map(this::buildUpdateForAddress)
        .collect(Collectors.toList());

    when(pendingOrganizationRepository.findAllById(findIds)).thenReturn(
        List.of(buildOrganizationForAddress("a1"))
    );
    // WHEN
    var actualKnown = pendingOrganizationService.findKnownPendingAddresses(
        updateOrganizationsTypes);
    // THEN
    assertThat(actualKnown).containsExactlyInAnyOrderElementsOf(List.of("a1"));
  }

}
