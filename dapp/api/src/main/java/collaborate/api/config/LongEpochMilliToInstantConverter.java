package collaborate.api.config;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.time.Instant;

public class LongEpochMilliToInstantConverter extends StdConverter<Long, Instant> {

  public Instant convert(final Long value) {
    return Instant.ofEpochMilli(value);
  }
}
