package collaborate.api.http;

import java.util.Base64;
import lombok.Data;

@Data
public class BasicAuthHeader {

  private final String value;

  public BasicAuthHeader(String user, String password) {
    value = toBase64(user, password);
  }

  String toBase64(String user, String password) {
    var basicToken = user + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes());
  }

}
