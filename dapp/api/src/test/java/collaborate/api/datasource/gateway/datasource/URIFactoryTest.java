package collaborate.api.datasource.gateway.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.URIFactory;
import collaborate.api.datasource.model.dto.web.QueryParam;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class URIFactoryTest {

  private final URIFactory uriFactory = new URIFactory();
  final ArrayList<QueryParam> queryParams = new ArrayList<>(List.of(
      QueryParam.builder().key("keyA").value("valueA").build(),
      QueryParam.builder().key("keyB").value("valueB").build()
  ));

  @ParameterizedTest
  @MethodSource("create_withSlashesParameters")
  void create_withSlashes(String baseUrl, String resourceUrlSuffix) {
    // GIVEN
    var datasource = WebServerDatasourceDTO.builder()
        .baseUrl(baseUrl)
        .build();
    var resource = WebServerResource.builder()
        .url(resourceUrlSuffix)
        .build();
    // WHEN
    URI actualURI = uriFactory.create(datasource, resource);
    // THEN
    assertThat(actualURI).hasToString("https://www.test.com/path");
  }

  private static Stream<Arguments> create_withSlashesParameters() {
    return Stream.of(
        Arguments.of("https://www.test.com/", "path"),
        Arguments.of("https://www.test.com", "path"),
        Arguments.of("https://www.test.com/", "/path")
    );
  }

  @Test
  void create_shouldHaveExpectedPath_withResourceQueryParams() {
    // GIVEN
    var resource = WebServerResource.builder()
        .url("/path")
        .queryParams(queryParams)
        .build();
    var datasource = WebServerDatasourceDTO.builder()
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
    var datasource = WebServerDatasourceDTO.builder()
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
    var datasource = WebServerDatasourceDTO.builder()
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
