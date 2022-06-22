package collaborate.api.datasource.kpi.find;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiQuery {

  @Schema(description =
      "The format to use for converting the labels. Mostly used when label is a date field.<br>"
          + "Valid format depends on the relying database. For full postgresql details see <a href=\"https://www.postgresql.org/docs/8.1/functions-formatting.html\">functions-formatting</a>"
      , example = "YYYY-MM")
  private String labelFormat;

  @Schema(description = "First Kpi aggregation field", example = "organizationWallet", required = true)
  @NotEmpty
  private String dataSetsGroup;

  @Schema(description = "Second Kpi aggregation field", example = "createdAt", required = true)
  @NotEmpty
  private String labelGroup;

  @Schema(description = "Predicate to filter data before computing the aggregations")
  private Set<SearchCriteria> search;

}
