package collaborate.api.date;


import static java.time.OffsetDateTime.now;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DateFormatterFactory {

  private final Clock clock;

  DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
  DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

  public String forPattern(String datePattern) {
    return DateTimeFormatter.ofPattern(datePattern).format(now(clock));
  }

  public String forDay() {
    return dayFormatter.format(now(clock));
  }

  public String forMonth() {
    return monthFormatter.format(now(clock));
  }

  public String forYear() {
    return yearFormatter.format(now(clock));
  }
}
