package collaborate.api.datasource.model.dto;

import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
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

  @Schema(
      description = "The kind of authentication used by the datasource",
      example = "CertificateBasedBasicAuth",
      required = true)
  String authenticationType;

  @Schema(description = "The datasource type.", example = "web", required = true)
  String datasourceType;

  @Schema(description = "The URI of the datasource.", example = "URI", required = true)
  String baseURI;

  @Schema(description = "List of scopes.", example = "address email", required = true)
  Set<String> listOfScopes;

  @Schema(description = "The name of the datasource.", example = "Company assets", required = true)
  String name;

  @Schema(description = "Information for making partners able to ask access to the datasource", required = true)
  PartnerTransferMethod partnerTransferMethod;
}
