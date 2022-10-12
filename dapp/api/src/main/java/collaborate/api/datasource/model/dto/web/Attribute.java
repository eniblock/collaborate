package collaborate.api.datasource.model.dto.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Attribute {

  public static final String ATTR_JWT_SCOPE = "scope";

  private String name;
  private String value;
  private String type;

  @JsonIgnore
  public static Optional<Attribute> findFirstByName(Collection<Attribute> keywords,
      String name) {
    if (keywords != null) {
      return keywords.stream()
          .filter(attr -> StringUtils.equals(attr.getName(), name))
          .findFirst();
    }
    return Optional.empty();
  }

  @JsonIgnore
  public static boolean containsName(Collection<Attribute> keywords, String name) {
    if (keywords != null) {
      return keywords.stream().anyMatch(k -> k.getName().equals(name));
    }
    return false;
  }
}
