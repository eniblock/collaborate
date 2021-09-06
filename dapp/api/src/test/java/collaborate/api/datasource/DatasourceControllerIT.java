package collaborate.api.datasource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.config.KeycloakTestConfig;
import collaborate.api.config.NoSecurityTestConfig;
import collaborate.api.datasource.domain.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.domain.web.OAuth2DatasourceFeatures;
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

@WebMvcTest(DatasourceController.class)
@ContextConfiguration(classes = {
    DatasourceController.class,
    KeycloakTestConfig.class,
    NoSecurityTestConfig.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class DatasourceControllerIT {

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
    mockMvc.perform(
        multipart(API_V1_DATASOURCES)
            .file(oAuth2Datasource)
        // THEN
    ).andExpect(status().isCreated());
  }


  @Test
  void postDatasource_shouldReturnCreated_withCertifacteBasedBasicAuth()
      throws Exception {
    // GIVEN
    when(datasourceService.testConnection(any(), any())).thenReturn(true);
    // WHEN
    mockMvc.perform(
        multipart(API_V1_DATASOURCES)
            .file(pfxFile)
            .file(basicAuthDatasource)
    )// THEN
        .andExpect(status().isCreated());
  }

  @Test
  void postDatasource_shouldReturnBadRequest_withTestConnectionFailure()
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
