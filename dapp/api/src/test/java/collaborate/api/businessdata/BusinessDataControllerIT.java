package collaborate.api.businessdata;

import static collaborate.api.test.TestResources.asJsonString;
import static collaborate.api.test.TestResources.readContent;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.businessdata.access.AccessRequestService;
import collaborate.api.businessdata.document.DocumentService;
import collaborate.api.businessdata.find.FindBusinessDataService;
import collaborate.api.config.ControllerExceptionHandler;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import java.nio.charset.StandardCharsets;
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
    classes = {
        BusinessDataController.class,
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        ControllerExceptionHandler.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class BusinessDataControllerIT {

  @MockBean
  AccessRequestService accessRequestService;
  @MockBean
  FindBusinessDataService findBusinessDataService;
  @MockBean
  DocumentService documentService;

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .alwaysDo(print())
        .build();
  }

  @Test
  void grantAccess_shouldResultInBadRequest_withNullBody() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/access-request"))
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void grantAccess_shouldResultInBadRequest_withEmptyListBody() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/access-request")
            .content(asJsonString(emptyList()))
            .contentType(APPLICATION_JSON)
        )
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void grantAccess_shouldResultInBadRequest_withInvalidListElement() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/access-request")
            .content(asJsonString(List.of(AssetDetailsDTO.builder()
                .build()
            )))
            .contentType(APPLICATION_JSON)
        )
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void grantAccess_shouldResultInOk_withValidListElement() throws Exception {
    // GIVEN
    var assetDetails = List.of(
        readContent("/businessdata/asset-details.json", AssetDetailsDTO.class)
    );
    when(accessRequestService.requestAccess(assetDetails)).thenReturn(null);
    // WHEN
    mockMvc
        .perform(post("/api/v1/business-data/access-request")
            .content(asJsonString(assetDetails))
            .contentType(APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
        )
        // THEN
        .andExpect(status().isOk());
  }

}
