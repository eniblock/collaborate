package collaborate.api.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class EMailDTO {

  private String from;
  private String to;
  private String subject;
  private Map<String, String> contextVariables = new HashMap<>();

  @JsonIgnore
  public String getContextVariable(String key) {
    return contextVariables.get(key);
  }

}
