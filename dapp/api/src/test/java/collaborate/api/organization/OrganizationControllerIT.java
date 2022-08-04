package collaborate.api.organization;

import static collaborate.api.test.TestResources.asJsonString;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.config.api.SmartContractConfig;
import collaborate.api.config.exception.ControllerExceptionHandler;
import collaborate.api.datasource.businessdata.BusinessDataController;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import java.util.Optional;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(BusinessDataController.class)
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        SmartContractAddressProperties.class,
        SmartContractConfig.class,
        OrganizationController.class,
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        ControllerExceptionHandler.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class OrganizationControllerIT {

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private OrganizationService organizationService;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .alwaysDo(print())
        .build();
  }

  @Test
  void addOrganization_shouldResultInBadRequest_withNullBody() throws Exception {
    // GIVEN
    // WHEN
    mockMvc
        .perform(post("/api/v1/organizations"))
        // THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void addOrganization_shouldResultInBadRequestAndExpectedMessage_withInvalidEncryptionKeyLength() throws Exception {
    // GIVEN
    var organization =OrganizationFeature.validOrganization.toBuilder()
        .encryptionKey("enc")
        .build();
    // WHEN
    var mockMvcResult = mockMvc
        .perform(post("/api/v1/organizations")
            .content(asJsonString(organization))
            .contentType(APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(status().isBadRequest())
        .andReturn();

    assertThat(mockMvcResult.getResponse().getContentAsString())
        .contains("The RSA public key length should be 392 characters long");
  }

  @Test
  void addOrganization_shouldResultInBadRequestAndExpectedMessage_withAddressAlreadyUsed() throws Exception {
    // GIVEN
    var organization = OrganizationFeature.validOrganization
        .toBuilder()
        .active(true)
        .build();

    when(organizationService.findOrganizationByPublicKeyHash(organization.getAddress()))
        .thenReturn(Optional.of(organization));
    // WHEN
    var mockMvcResult = mockMvc
        .perform(post("/api/v1/organizations")
            .content(asJsonString(organization))
            .contentType(APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(status().isBadRequest())
        .andReturn();

    assertThat(mockMvcResult.getResponse().getContentAsString())
        .contains("The wallet address is already used by another organization");
  }

  @Test
  void addOrganization_shouldResultInBadRequestAndExpectedMessage_withLegalNameAlreadyUsed() throws Exception {
    // GIVEN
    var organization = OrganizationFeature.validOrganization
        .toBuilder()
        .active(true)
        .build();
    when(organizationService.findByLegalName(organization.getLegalName()))
        .thenReturn(Optional.of(organization));
    // WHEN
    var mockMvcResult = mockMvc
        .perform(post("/api/v1/organizations")
            .content(asJsonString(organization))
            .contentType(APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(status().isBadRequest())
        .andReturn();

    assertThat(mockMvcResult.getResponse().getContentAsString())
        .contains("The organization name is already used by another organization.");
  }

  @Test
  void addOrganization_shouldResultInCreated_withValidOrganization() throws Exception {
    // GIVEN
    var organization = OrganizationFeature.validOrganization;
    doNothing().when(organizationService).upsertOrganization(organization);
    // WHEN
    var mockMvcResult = mockMvc
        .perform(post("/api/v1/organizations")
            .content(asJsonString(organization))
            .contentType(APPLICATION_JSON)
        )
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(status().isCreated())
        .andReturn();
  }

}
