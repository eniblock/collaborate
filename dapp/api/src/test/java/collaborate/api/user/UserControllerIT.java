package collaborate.api.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import collaborate.api.user.connected.ConnectedUserService;
import collaborate.api.user.model.UserDTO;
import collaborate.api.user.security.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(UserController.class)
@ContextConfiguration(
    classes = {UserController.class, KeycloakTestConfig.class, NoSecurityTestConfig.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class UserControllerIT {

  @MockBean
  KeycloakService keycloakService;
  @MockBean
  UserService userService;
  @MockBean
  ConnectedUserService connectedUserService;
  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();
  }

  @Test
  void createTagUser_shouldReturn201Created_withTagUserCreationSuccess() throws Exception {
    // GIVEN
    UserDTO usersDTO = new UserDTO();
    when(connectedUserService.updateWithAssetOwnerRole()).thenReturn(usersDTO);
    // WHEN
    mockMvc
        .perform(post("/api/v1/users/tag/asset-owner"))
        // THEN
        .andExpect(status().isOk());
  }
}
