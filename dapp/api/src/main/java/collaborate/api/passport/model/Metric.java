package collaborate.api.passport.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Metric {

  @Schema(description = "Last moment when the data has been fetched")
  @ISO8601JsonStringFormat
  private ZonedDateTime updatedAt;
  @Schema(description = "The kind of data")
  private String scope;
  @Schema(description = "The value for this kind of data")
  private JsonNode value;
}
