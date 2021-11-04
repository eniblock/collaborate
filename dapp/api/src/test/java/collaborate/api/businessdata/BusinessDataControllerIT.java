package collaborate.api.businessdata;

import static collaborate.api.test.TestResources.asJsonString;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.businessdata.find.FindBusinessDataService;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import java.util.List;
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

@WebMvcTest(BusinessDataController.class)
@ContextConfiguration(
    classes = {BusinessDataController.class, KeycloakTestConfig.class, NoSecurityTestConfig.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class BusinessDataControllerIT {

  @MockBean
  FindBusinessDataService findBusinessDataService;

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();
  }

  @Test
  void grantAccess_shouldResultInBadRequest_withNullBody() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/grant-access"))
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void grantAccess_shouldResultInBadRequest_withEmptyListBody() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/grant-access")
            .content(asJsonString(emptyList()))
            .contentType(APPLICATION_JSON)
        )
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void grantAccess_shouldResultInBadRequest_withEmptyListElement() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/grant-access")
            .content(asJsonString(List.of("")))
            .contentType(APPLICATION_JSON)
        )
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void grantAccess_shouldResultInOk_withValidListElement() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/grant-access")
            .content(asJsonString(List.of(1, 2)))
            .contentType(APPLICATION_JSON)
        )
        // THEN
        .andExpect(status().isOk());
  }
}
