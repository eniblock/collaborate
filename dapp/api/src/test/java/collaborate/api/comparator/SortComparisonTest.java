package collaborate.api.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

class SortComparisonTest {

  SortComparison sortComparison = new SortComparison();

  @Data
  @AllArgsConstructor
  public static class Person {

    Integer age;
    String name;
    String surname;
  }

  @Test
  void sortComparator() {
    // GIVEN
    var expected = List.of(
        new Person(21, "Pierre", null),
        new Person(22, "Paul", null),
        new Person(22, "Paul", "Popo"),
        new Person(23, "Corentin", null),
        new Person(23, "Benjamin", "Benji"),
        new Person(23, "Benjamin", "Benou")
    );
    var sort = Sort.by(
        Order.asc("age"),
        Order.desc("name"),
        Order.asc("surname")
    );

    // WHEN
    var shuffled = new ArrayList<>(expected);
    Collections.shuffle(shuffled);

    var actual = shuffled.stream()
        .sorted(sortComparison.sortComparator(Person.class, sort).get())
        .collect(Collectors.toList());

    // THEN
    assertThat(actual).containsExactlyElementsOf(expected);
  }

  @Test
  void sortComparator_shouldThrowSortException_withUnknowProperty() {
    // THEN
    var sort = Sort.by(
        Order.asc("unknown")
    );
    // WHEN THEN
    var thrown = Assertions.assertThrows(SortException.class, () -> {
      sortComparison.sortComparator(Person.class, sort);
    });
    assertThat(thrown).hasMessage("No property accessor for 'unknown'");
  }

}
