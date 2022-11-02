package collaborate.api.datasource.servicedata.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.datasource.servicedata.model.ServiceDataDTOElement;
import collaborate.api.organization.model.OrganizationDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDataAssetDetailsDTO {

  @Schema(description = "The id of the asset", example = "5NPET4AC8AH593530")
  @NotBlank
  private String id;

  @Schema(description = "The id of the asset", example = "5NPET4AC8AH593530")
  @NotBlank
  private String assetId;

  @Schema(description = "The asset catalog of datasources")
  @Valid
  @NotNull
  private List<@Valid ServiceDataDTOElement> services;

  @Schema(description = "The owner of the asset")
  @Valid
  @NotNull
  private OrganizationDTO assetOwner;

  @Schema(description = "The asset creation datetime (ISO8601 = yyyy-MM-dd'T'HH:mm:ss.SSSXXX)")
  @ISO8601JsonStringFormat
  private ZonedDateTime creationDatetime;

  @Schema(description = "The NFT operator")
  private OrganizationDTO operator;

  @Schema(description = "The Id of the NFT passport (null if it is not minted)", example = "2")
  private Integer tokenId;

  @Schema(description = "The token status of the the asset", example = "PENDING_CREATION")
  private TokenStatus tokenStatus;

}
