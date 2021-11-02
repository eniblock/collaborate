package collaborate.api.datasource.model.dto;

import collaborate.api.config.ISO8601JsonStringFormat;
import collaborate.api.datasource.model.dto.enumeration.DatasourceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListDatasourceDTO {

  @Schema(
      description = "The creation date of the datasource.",
      example = "2019-03-27T10:15:30",
      required = true)
  @ISO8601JsonStringFormat
  ZonedDateTime creationDateTime;

  @Schema(description = "The datasource type.", example = "web", required = true)
  String datasourceType;

  @Schema(
      description = "The id of the datasource.",
      example = "fa9a8a6c-1aea-4086-8a00-64c5c959c0fl",
      required = true)
  String id;

  @Schema(description = "The name of the datasource.", example = "PSA ds", required = true)
  String name;

  @Schema(
      description = "The purpose of the datasource.",
      example = "digital passport",
      required = true)
  List<String> purpose;

  @Schema(
      description = "The number of GrantedAccess of the datasource.",
      example = "4")
  Integer nbGrantedAccess;

  @Schema(description = "The status of the datasource.", example = "1", required = true)
  DatasourceStatus status = DatasourceStatus.CREATED;
}
