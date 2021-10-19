package collaborate.api.config;

import static java.util.Collections.emptySet;

import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

  private static final String SPLIT_CHAR = ";";

  @Override
  public String convertToDatabaseColumn(Set<String> stringList) {
    return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
  }

  @Override
  public Set<String> convertToEntityAttribute(String string) {
    return string != null ? Set.of(string.split(SPLIT_CHAR)) : emptySet();
  }
}
