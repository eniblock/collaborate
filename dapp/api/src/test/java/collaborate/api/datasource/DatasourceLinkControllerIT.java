package collaborate.api.datasource;

import static collaborate.api.test.TestResources.objectMapper;
import static collaborate.api.test.TestResources.readPath;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.model.dto.web.OAuth2DatasourceFeatures;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(DatasourceController.class)
@ContextConfiguration(classes = {
    DatasourceController.class,
    KeycloakTestConfig.class,
    NoSecurityTestConfig.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class DatasourceLinkControllerIT {

  public static final String API_V1_DATASOURCES = "/api/v1/datasources";
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  TestConnectionFactory testConnectionFactory;
  @MockBean
  DatasourceService datasourceService;

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
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    var mvcResult = mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(oAuth2Datasource)

        ).andExpect(request().asyncStarted())
        .andDo(MockMvcResultHandlers.log())
        .andReturn();
    // THEN
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isCreated());
  }


  @Test
  void create_shouldReturnBadRequest_withMissingDatasourceKeyword()
      throws Exception {
    // GIVEN
    var datasource = readPath("/datasource/domain/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setKeywords(new HashSet<>(Set.of("invalid-keyword")));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
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
    var datasource = readPath("/datasource/domain/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setResources(List.of(new WebServerResource()));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
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
    var datasource = readPath("/datasource/domain/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setKeywords(new HashSet<>(Set.of("business-data")));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    var mvcResult = mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        ).andExpect(request().asyncStarted())
        .andDo(MockMvcResultHandlers.log())
        .andReturn();
    // THEN
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isCreated());
  }

  @Test
  void create_shouldReturnBadRequest_withValidDigitalPassportKeyword()
      throws Exception {
    // GIVEN
    var datasource = readPath("/datasource/domain/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
    datasource.setKeywords(new HashSet<>(Set.of("digital-passport")));
    var datasourceJson = objectMapper.writeValueAsString(datasource);
    final MockMultipartFile basicAuthDatasource = new MockMultipartFile(
        "datasource",
        "",
        APPLICATION_JSON,
        datasourceJson.getBytes()
    );
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    var mvcResult = mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        ).andExpect(request().asyncStarted())
        .andDo(MockMvcResultHandlers.log())
        .andReturn();
    // THEN    // THEN
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isCreated());
  }

  @Test
  void create_shouldReturnCreated_withValidCertificateBasedDatasource()
      throws Exception {
    // GIVEN
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
    when(datasourceService.create(any(), any())).thenReturn(new Datasource());

    // WHEN
    var mvcResult = mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        ).andExpect(request().asyncStarted())
        .andDo(MockMvcResultHandlers.log())
        .andReturn();
    // THEN
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isCreated());
  }

  @Test
  void create_shouldReturnBadRequest_withTestConnectionFailure()
      throws Exception {
    // GIVEN
    when(datasourceService.testConnection(any(), any())).thenReturn(false);
    // WHEN
    mockMvc.perform(
            multipart(API_V1_DATASOURCES)
                .file(pfxFile)
                .file(basicAuthDatasource)
        )// THEN
        .andExpect(status().isBadRequest());
  }

}
