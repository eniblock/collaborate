package collaborate.api.datasource.businessdata;

import static collaborate.api.test.TestResources.asJsonString;
import static collaborate.api.test.TestResources.readContent;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.config.api.SmartContractConfig;
import collaborate.api.config.exception.ControllerExceptionHandler;
import collaborate.api.datasource.businessdata.access.AccessRequestService;
import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.document.AssetsService;
import collaborate.api.datasource.businessdata.find.AssetDetailsService;
import collaborate.api.datasource.businessdata.find.BusinessDataNftIndexerService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(BusinessDataController.class)
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        SmartContractAddressProperties.class,
        SmartContractConfig.class,
        BusinessDataController.class,
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        ControllerExceptionHandler.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class BusinessDataControllerIT {

  @MockBean
  ApiProperties apiProperties;
  @MockBean
  AccessRequestService accessRequestService;
  @MockBean
  AssetDetailsService assetDetailsService;
  @MockBean
  NftDatasourceService nftDatasourceService;
  @MockBean
  BusinessDataNftIndexerService businessDataNftIndexerService;
  @MockBean
  AssetsService assetsService;

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
        readContent("/datasource/businessdata/access/request/access-request-dto.json", AccessRequestDTO.class)
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
