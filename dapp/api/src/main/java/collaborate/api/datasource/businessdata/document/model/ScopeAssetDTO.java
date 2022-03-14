package collaborate.api.datasource.businessdata.document.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScopeAssetDTO {

  @Schema(description = "The item displayable name", example = "List of the available products")
  private String name;
  // FIXME remove ?
  @Schema(description = "Deprecated", example = "MVP Document")
  private String type;
  @Schema(description = "When this asset has been seen. Use ISO8601 format", example = "2022-03-14T11:13:32.851Z")
  @ISO8601JsonStringFormat
  private ZonedDateTime synchronizedDate;
  @Schema(description = "The asset download link", example = "https://dsp-a.fds.pcc.eniblock.fr/customers-analytics/clients/download")
  private URI downloadLink;
}
