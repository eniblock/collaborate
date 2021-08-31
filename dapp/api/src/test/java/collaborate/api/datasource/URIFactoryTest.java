package collaborate.api.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.domain.web.QueryParam;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.WebServerResource;
import collaborate.api.datasource.domain.web.authentication.BasicAuth;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

class URIFactoryTest {

  private final URIFactory uriFactory = new URIFactory();
  final List<QueryParam> queryParams = List.of(
      QueryParam.builder().key("keyA").value("valueA").build(),
      QueryParam.builder().key("keyB").value("valueB").build()
  );

  @Test
  void create_shouldHaveExpectedPath() {
    // GIVEN
    var resource = WebServerResource.builder()
        .url("path")
        .build();
    var datasource = WebServerDatasource.builder()
        .baseUrl("https://www.test.com/")
        .build();
    ;
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI).hasToString("https://www.test.com/path");
  }

  @Test
  void create_shouldAddNecessaryPath_withoutSlahAtEndOfDatasourceNorAtBeginningOfResourcePath() {
    // GIVEN
    var resource = WebServerResource.builder()
        .url("path")
        .build();
    var datasource = WebServerDatasource.builder()
        .baseUrl("https://www.test.com")
        .build();
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI).hasToString("https://www.test.com/path");
  }

  @Test
  void create_shouldRemoveUnecessaryPath_withUnecessarySlashInResourcePath() {
    // GIVEN
    var resource = WebServerResource.builder()
        .url("/path")
        .build();
    var datasource = WebServerDatasource.builder()
        .baseUrl("https://www.test.com/")
        .build();
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI).hasToString("https://www.test.com/path");
  }

  @Test
  void create_shouldHaveExpectedPath_withResourceQueryParams() {
    // GIVEN
    var resource = WebServerResource.builder()
        .url("/path")
        .queryParams(queryParams)
        .build();
    var datasource = WebServerDatasource.builder()
        .baseUrl("https://www.test.com/")
        .build();
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI).hasToString("https://www.test.com/path?keyA=valueA&keyB=valueB");
  }

  @Test
  void create_shouldHaveExpectedPath_withAuthQueryParams() {
    // GIVEN
    var datasource = WebServerDatasource.builder()
        .baseUrl("https://www.test.com/")
        .build();
    datasource.setAuthMethod(new BasicAuth("", "", queryParams));
    var resource = WebServerResource.builder()
        .url("/path")
        .build();
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI).hasToString("https://www.test.com/path?keyA=valueA&keyB=valueB");
  }

  @Test
  void create_shouldHaveExpectedPath_withAuthQueryParamsAndResourceQueryParams() {
    // GIVEN
    var datasource = WebServerDatasource.builder()
        .baseUrl("https://www.test.com/")
        .build();
    datasource.setAuthMethod(new BasicAuth("", "", queryParams));
    var resource = WebServerResource.builder()
        .url("/path")
        .queryParams(queryParams)
        .build();
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI)
        .hasToString("https://www.test.com/path?keyA=valueA&keyA=valueA&keyB=valueB&keyB=valueB");
  }
}
