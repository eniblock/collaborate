package collaborate.api.datasource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.http.HttpURLConnectionVisitorFactory;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestConnectionVisitorTest {

  @Mock
  URIFactory uriFactory;
  @Mock
  HttpURLConnectionVisitorFactory httpURLConnectionVisitorFactory;
  @InjectMocks
  TestConnectionVisitor testConnectionVisitor;

  @Test
  void create_shouldCallUriFactoryWithExpectedResource_withWebServerDatasourceContainingPurposeTestConnectionResource()
      throws Exception {
    // GIVEN
    var expectedResource = WebServerResource.builder()
        .url("myExpectedResourceUrl")
        .keywords(Set.of("scope:list-asset"))
        .build();
    String baseUrl = "http://baseUrl";
    WebServerDatasourceDTO datasource = WebServerDatasourceDTO
        .builder()
        .baseUrl(baseUrl)
        .resources(List.of(expectedResource))
        .authMethod(new BasicAuth())
        .build();
    when(uriFactory.create(datasource, expectedResource)).thenCallRealMethod();
    when(httpURLConnectionVisitorFactory.create(
        URI.create(baseUrl + "/myExpectedResourceUrl"))).thenCallRealMethod();
    // WHEN
    datasource.accept(testConnectionVisitor);
    // THEN
    verify(uriFactory, times(1)).create(datasource, expectedResource);
  }

  @Test
  void create_shouldThrowIllegalStateException_withWebServerDatasourceNotContainingPurposeTestConnectionResource() {
    // GIVEN
    var resourceA = WebServerResource.builder()
        .url("resourceUrlA")
        .keywords(Set.of("test-connection"))
        .build();
    var resourceB = WebServerResource.builder()
        .url("resourceUrlB")
        .keywords(Set.of("assets"))
        .build();
    WebServerDatasourceDTO datasource = WebServerDatasourceDTO
        .builder()
        .resources(List.of(resourceA, resourceB))
        .build();

    // THEN
    Assertions.assertThrows(IllegalStateException.class, () -> {
      // WHEN
      datasource.accept(testConnectionVisitor);
    });

    verify(uriFactory, times(0)).create(any(), any());
  }
}
