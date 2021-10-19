package collaborate.api.datasource.model.dto;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum DatasourcePurpose {
  BUSINESS_DATA("business-data"),
  DIGITAL_PASSPORT("digital-passport");

  private final String keyword;

  DatasourcePurpose(String keyword) {
    this.keyword = keyword;
  }

  @JsonCreator
  public static DatasourcePurpose getByKeyword(String keyword) {
    for (final DatasourcePurpose oneType : DatasourcePurpose.values()) {
      if (oneType.keyword.equals(keyword)) {
        return oneType;
      }
    }
    throw new IllegalStateException(format("No type for keyword=%s", keyword));
  }

  public static Set<String> getKeywords() {
    return Arrays.stream(DatasourcePurpose.values())
        .map(DatasourcePurpose::getKeyword)
        .collect(Collectors.toSet());
  }
}

