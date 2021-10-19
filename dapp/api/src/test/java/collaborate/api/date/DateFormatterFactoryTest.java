package collaborate.api.date;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class DateFormatterFactoryTest {

  Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
  DateFormatterFactory dateFormatterFactory = new DateFormatterFactory(clock);

  @Test
  void forPattern_shouldReturnExpected() {
    // GIVEN
    String pattern = "yyyy";
    // WHEN
    var dateResult = dateFormatterFactory.forPattern(pattern);
    // THEN
    assertThat(dateResult).isEqualTo("2018");
  }

  @Test
  void forYear_shouldReturnExpected() {
    // GIVEN
    // WHEN
    var dateResult = dateFormatterFactory.forYear();
    // THEN
    assertThat(dateResult).isEqualTo("2018");
  }

  @Test
  void forMonth_shouldReturnExpected() {
    // GIVEN
    // WHEN
    var dateResult = dateFormatterFactory.forMonth();
    // THEN
    assertThat(dateResult).isEqualTo("2018-08");
  }

  @Test
  void forDay_shouldReturnExpected() {
    // GIVEN
    // WHEN
    var dateResult = dateFormatterFactory.forDay();
    // THEN
    assertThat(dateResult).isEqualTo("2018-08-19");
  }
}
