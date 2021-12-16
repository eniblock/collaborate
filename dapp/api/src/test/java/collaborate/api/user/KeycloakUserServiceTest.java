package collaborate.api.user;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import collaborate.api.user.model.UserDTO;
import collaborate.api.user.security.KeycloakUserService;
import collaborate.api.user.security.KeycloakUsersClient;
import collaborate.api.user.security.UserSearchCriteria;
import collaborate.api.user.security.UserSearchResponseDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class KeycloakUserServiceTest {

  private KeycloakUsersClient keycloakController;
  private UserSearchResponseDTO userSearchResponseDTO;

  @Before
  public void init() {
    userSearchResponseDTO = mock(UserSearchResponseDTO.class);
    keycloakController = mock(KeycloakUsersClient.class);
  }

  @Test
  public void findAllWithList() {
    when(userSearchResponseDTO.getContent()).thenReturn(null);
    when(keycloakController.findByCriteria(any(UserSearchCriteria.class)))
        .thenReturn(userSearchResponseDTO);

    KeycloakUserService keycloakUserService = new KeycloakUserService(keycloakController);

    List<UserDTO> result = keycloakUserService.findAll();

    assertEquals(0, result.size());
  }

  @Test
  public void findAllWithListWhenResponseNotNull() {
    UserDTO user = new UserDTO();
    List<UserDTO> response = new ArrayList<>();
    response.add(user);

    when(userSearchResponseDTO.getContent()).thenReturn(response);
    when(keycloakController.findByCriteria(any(UserSearchCriteria.class)))
        .thenReturn(userSearchResponseDTO);

    KeycloakUserService keycloakUserService = new KeycloakUserService(keycloakController);

    List<UserDTO> result = keycloakUserService.findAll();

    assertEquals(result, response);
    assertEquals(result.get(0), user);
  }

}
