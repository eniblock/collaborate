package collaborate.api.services;

import collaborate.api.restclient.IKeycloakController;
import collaborate.api.services.dto.UserDTO;
import collaborate.api.services.dto.UserSearchCriteria;
import collaborate.api.services.dto.UserSearchResponseDTO;

import org.junit.Before;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KeycloakServiceTest {
    private IKeycloakController keycloakController;
	private Keycloak keycloak;
    private TokenManager tokenManager;
    private UserSearchResponseDTO userSearchResponseDTO;

    private String FAKE_TOKEN = "fake token";

    @Before
    public void init() {
        tokenManager = mock(TokenManager.class);
        userSearchResponseDTO = mock(UserSearchResponseDTO.class);
        keycloak = mock(Keycloak.class);
        keycloakController = mock(IKeycloakController.class);
    }

    @Test
    public void findAllWithList() {
        when(userSearchResponseDTO.getContent()).thenReturn(null);
        when(keycloakController.findByCriteria(anyString(), any(UserSearchCriteria.class))).thenReturn(userSearchResponseDTO);
        when(tokenManager.getAccessTokenString()).thenReturn(FAKE_TOKEN);
        when(keycloak.tokenManager()).thenReturn(tokenManager);

        KeycloakService keycloakService = new KeycloakService(keycloakController, keycloak);

        List<UserDTO> result = keycloakService.findAll();

        assertEquals(result.size(), 0);
    }

    @Test
    public void findAllWithListWhenResponseNotNull() {
        UserDTO user = new UserDTO();
        List<UserDTO> response = new ArrayList<>();
        response.add(user);

        when(userSearchResponseDTO.getContent()).thenReturn(response);
        when(keycloakController.findByCriteria(anyString(), any(UserSearchCriteria.class))).thenReturn(userSearchResponseDTO);
        when(tokenManager.getAccessTokenString()).thenReturn(FAKE_TOKEN);
        when(keycloak.tokenManager()).thenReturn(tokenManager);

        KeycloakService keycloakService = new KeycloakService(keycloakController, keycloak);

        List<UserDTO> result = keycloakService.findAll();

        assertEquals(result, response);
        assertEquals(result.get(0), user);
    }


}
