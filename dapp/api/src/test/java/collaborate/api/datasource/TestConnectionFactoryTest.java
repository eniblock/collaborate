package collaborate.api.datasource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.http.HttpURLConnectionFactory;
import collaborate.api.http.security.SSLContextException;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestConnectionFactoryTest {

  @Mock
  URIFactory uriFactory;
  @Mock
  HttpURLConnectionFactory httpURLConnectionFactory;
  @InjectMocks
  TestConnectionFactory testConnectionFactory;

  @Test
  void create_shouldCallUriFactoryWithExpectedResource_withWebServerDatasourceContainingPurposeTestConnectionResource()
      throws UnrecoverableKeyException, SSLContextException, IOException {
    // GIVEN
    var expectedResource = WebServerResource.builder()
        .url("myExpectedResourceUrl")
        .keywords(Set.of("purpose:test-connection"))
        .build();
    WebServerDatasourceDTO datasource = WebServerDatasourceDTO
        .builder()
        .resources(List.of(expectedResource))
        .build();
    // WHEN
    testConnectionFactory.create(datasource);
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
      testConnectionFactory.create(datasource);
    });

    verify(uriFactory, times(0)).create(any(), any());
  }
}
