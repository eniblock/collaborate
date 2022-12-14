package collaborate.api.datasource.gateway.datasource;

import static collaborate.api.test.TestResources.objectMapper;
import static collaborate.api.test.TestResources.readContent;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.datasource.DatasourceController;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.TestConnectionVisitor;
import collaborate.api.datasource.create.CreateDatasourceService;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.model.dto.web.OAuth2DatasourceFeatures;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import collaborate.api.validation.ValidationService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(DatasourceController.class)
@ContextConfiguration(classes = {
    DatasourceController.class,
    KeycloakTestConfig.class,
    NoSecurityTestConfig.class,
    ValidationService.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"default", "test"})
class DatasourceControllerIT {

  public static final String API_V1_DATASOURCES = "/api/v1/datasources";
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  TestConnectionVisitor testConnectionVisitor;
  @MockBean
  DatasourceService datasourceService;
  @MockBean
  CreateDatasourceService createDatasourceService;

  final MockMultipartFile oAuth2Datasource = new MockMultipartFile(
      "datasource",
      "",
      APPLICATION_JSON,
      OAuth2DatasourceFeatures.datasourceJson.getBytes()
  );
  final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
      "datasource",
      "",
      APPLICATION_JSON,
      CertificateBasedBasicAuthDatasourceFeatures.datasourceJson.getBytes()
  );
  final MockMultipartFile pfxFile = new MockMultipartFile(
      "pfxFile",
      "hello.txt",
      TEXT_PLAIN_VALUE,
      "Hello, World!".getBytes()
  );

  @Test
  void postDatasource_shouldReturnCreatedStatus_withOAuth2() throws Exception {
    // GIVEN
    when(createDatasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(oAuth2Datasource)
        )
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isCreated());
  }


  @Test
  void create_shouldReturnBadRequest_withMissingDatasourceKeyword()
      throws Exception {
    // GIVEN
    var datasource = readContent("/datasource/model/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setKeywords(new HashSet<>(Set.of("invalid-keyword")));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(createDatasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        )// THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnBadRequest_withNoResourceHavingPurposeTestConnectionKeyword()
      throws Exception {
    // GIVEN
    var datasource = readContent("/datasource/model/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setResources(List.of(new WebServerResource()));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(createDatasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        )// THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnCreated_withValidBusinessDataKeyword()
      throws Exception {
    // GIVEN
    var datasource = readContent("/datasource/model/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setKeywords(new HashSet<>(Set.of("business-data")));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(createDatasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        ).andDo(MockMvcResultHandlers.log())
        .andExpect(status().isCreated());
  }

  @Test
  void create_shouldReturnBadRequest_withValidDigitalPassportKeyword()
      throws Exception {
    // GIVEN
    var datasource = readContent("/datasource/model/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setKeywords(new HashSet<>(Set.of("digital-passport")));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(createDatasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        ).andDo(MockMvcResultHandlers.log())
        .andExpect(status().isCreated());
  }

  @Test
  void create_shouldReturnCreated_withValidCertificateBasedDatasource()
      throws Exception {
    // GIVEN
    when(createDatasourceService.testConnection(any(), any())).thenReturn(true);
    when(createDatasourceService.create(any(), any())).thenReturn(null);

    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        ).andDo(MockMvcResultHandlers.log())
        .andExpect(status().isCreated());
  }

  @Test
  void create_shouldReturnBadRequest_withTestConnectionFailure()
      throws Exception {
    // GIVEN
    when(createDatasourceService.testConnection(any(), any())).thenReturn(false);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        )// THEN
        .andExpect(status().isBadRequest());
  }

}
