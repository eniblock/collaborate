package collaborate.api.http.security;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.http.BasicAuthHeader;
import org.junit.jupiter.api.Test;

class BasicAuthHeaderTest {

  @Test
  void name() {
    // GIVEN
    String user = "username";
    String pwd = "password";

    // WHEN
    BasicAuthHeader basicAuthHeader = new BasicAuthHeader(user, pwd);
    // THEN
    assertThat(BasicAuthHeader.KEY).isEqualTo("Authorization");
    assertThat(basicAuthHeader.getValue()).isEqualTo("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
  }
}
