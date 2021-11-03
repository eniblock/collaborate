package collaborate.api;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.ServletContext;
import org.junit.jupiter.api.Test;
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

  @Test
  void contextLoads() {
    ServletContext servletContext = webApplicationContext.getServletContext();
    assertThat(servletContext).isNotNull()
        .isInstanceOf(MockServletContext.class);
  }
}
