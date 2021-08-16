package collaborate.api.datasource;


import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import collaborate.api.config.KeycloakTestConfig;
import collaborate.api.config.NoSecurityTestConfig;
import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.domain.web.OAuthDatasourceFeatures;

import collaborate.api.datasource.domain.web.WebServerDatasource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  DatasourceTestConnectionFactory datasourceTestConnectionFactory;
  @MockBean
  DatasourceService datasourceService;

  @Captor
  ArgumentCaptor<DataSource> datasourceCaptor;

  @Test
  void postDatasource_with_oauth_should_return_IS_CREATED() throws Exception {
    // GIVEN
    MediaType APPLICATION_JSON_UTF8 =
        new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            UTF_8
        );
    // WHEN
    mockMvc.perform(
            post("/api/v2/datasources/oauth")
                .contentType(APPLICATION_JSON_UTF8)
                .content(OAuthDatasourceFeatures.getInstance().datasourceJson.getBytes())

        )// THEN
        .andExpect(status().isCreated());
  }

  @Test
  void postDatasource_basicAuth_should_return_IS_CREATED_when_testConnection_succeed()
      throws Exception {
    // GIVEN
    when(datasourceService.testBasicAuthConnection(any(), any())).thenReturn(true);
    String pfxFileContent = "Hello, World!";
    MockMultipartFile file = new MockMultipartFile(
        "pfxFile",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        pfxFileContent.getBytes()
    );
    MockMultipartFile datasourceJson = new MockMultipartFile(
        "datasource",
        "",
        "application/json",
        CertificateBasedBasicAuthDatasourceFeatures.getInstance().datasourceJson.getBytes()
    );

    // WHEN
    mockMvc.perform(
            multipart("/api/v2/datasources/basic-auth")
                .file(file)
                .file(datasourceJson)
        )// THEN
        .andExpect(status().isCreated());
  }

  @Test
  void postDatasource_basicAuth_should_return_BAD_REQUEST_when_testConnection_fail()
      throws Exception {
    // GIVEN
    when(datasourceService.testBasicAuthConnection(any(), any())).thenReturn(false);
    String pfxFileContent = "Hello, World!";
    MockMultipartFile file = new MockMultipartFile(
        "pfxFile",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        pfxFileContent.getBytes()
    );
    MockMultipartFile datasourceJson = new MockMultipartFile(
        "datasource",
        "",
        "application/json",
        CertificateBasedBasicAuthDatasourceFeatures.getInstance().datasourceJson.getBytes()
    );

    // WHEN
    mockMvc.perform(
            multipart("/api/v2/datasources/basic-auth")
                .file(file)
                .file(datasourceJson)
        )// THEN
        .andExpect(status().isBadRequest());
  }

  @Test
  void testConnection_should_be_ok_with_good_parameters()
      throws Exception {
    // GIVEN
    String pfxFileContent = "Hello, World!";
    MockMultipartFile file = new MockMultipartFile(
        "pfxFile",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        pfxFileContent.getBytes()
    );
    MockMultipartFile datasourceJson = new MockMultipartFile(
        "datasource",
        "",
        "application/json",
        CertificateBasedBasicAuthDatasourceFeatures.getInstance().datasourceJson.getBytes()
    );
    when(datasourceService.testBasicAuthConnection(datasourceCaptor.capture(), any())).thenReturn(
        true);
    when(datasourceService.create(any())).thenReturn(new WebServerDatasource());
    // WHEN
    mockMvc.perform(
            multipart("/api/v2/datasources/test-connection")
                .file(file)
                .file(datasourceJson)
        )// THEN
        .andExpect(status().isOk());
  }
}
