package collaborate.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import collaborate.api.organization.OrganizationFeature;
import collaborate.api.user.UserFeature;
import javax.servlet.ServletContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles({"default", "test"})
class ApiApplicationIT {

  @Autowired
  private WebApplicationContext webApplicationContext;
  private static ClientAndServer mockServer;

  @BeforeAll
  static void startServer() {
    mockServer = startClientAndServer(1084);
    UserFeature.mockTagAdminUser(mockServer);
    OrganizationFeature.mockTagOrganizationsStorage(mockServer);
  }

  @AfterAll
  static void stopServer() {
    mockServer.stop();
  }

  @Test
  void contextLoads() {
    ServletContext servletContext = webApplicationContext.getServletContext();
    assertThat(servletContext).isNotNull()
        .isInstanceOf(MockServletContext.class);
  }
}
