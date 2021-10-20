package collaborate.api.datasource.model.dto;

import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasourceDetailsDto {

  @Schema(
      description = "The id of the datasource.",
      example = "fa9a8a6c-1aea-4086-8a00-64c5c959c0fl",
      required = true)
  String id;

  @Schema(description = "The name of the datasource.", example = "PSA ds", required = true)
  String name;

  @Schema(
      description = "The creation date of the datasource.",
      example = "2019-03-27T10:15:30",
      required = true)
  String certificateEmail;

  @Schema(description = "The datasource access method.", example = "web", required = true)
  String accessMethod;

  @Schema(
      description = "The id of the datasource.",
      example = "fa9a8a6c-1aea-4086-8a00-64c5c959c0fl",
      required = true)
  String authenticationType;

  @Schema(description = "The URI of the datasource.", example = "URI", required = true)
  String baseURI;

  @Schema(description = "List of scopes.", example = "address email", required = true)
  Set<String> listOfScopes;

  @Schema(description = "The datasource type.", example = "web", required = true)
  String datasourceType;

  @Schema(description = "certificateBasedBasicAuth", required = true)
  CertificateBasedBasicAuth certificateBasedBasicAuth;
}
