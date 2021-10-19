package collaborate.api.http.security;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.http.BasicAuthHeader;
import org.junit.jupiter.api.Test;

class BasicAuthHeaderTest {

  @Test
  void name() {
    // GIVEN
    String user = "MWPDRV01";
    String pwd = "BBrlKQ0i";

    // WHEN
    BasicAuthHeader basicAuthHeader = new BasicAuthHeader(user, pwd);
    // THEN
    assertThat(BasicAuthHeader.KEY).isEqualTo("Authorization");
    assertThat(basicAuthHeader.getValue()).isEqualTo("Basic TVdQRFJWMDE6QkJybEtRMGk=");
  }
}
