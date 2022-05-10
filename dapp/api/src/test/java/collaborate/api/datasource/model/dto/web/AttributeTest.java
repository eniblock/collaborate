package collaborate.api.datasource.model.dto.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

public class AttributeTest {

  @Test
  void name() {
    // GIVEN
    var expectedAttributes = Attribute.builder()
        .name("na")
        .value("va")
        .build();
    var attributes = List.of(expectedAttributes);
    // WHEN
    var result = Attribute.findFirstByName(attributes, "na");
    // THEN
    assertThat(result).hasValue(expectedAttributes);
  }
}
