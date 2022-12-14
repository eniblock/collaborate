package collaborate.api.datasource.nft.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.model.OrganizationDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
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
public class AssetDetailsDTO {

  @Schema(description = "The asset catalog of datasources")
  @Valid
  @NotNull
  private AssetDataCatalogDTO assetDataCatalog;

  @Schema(description = "The id of the asset", example = "5NPET4AC8AH593530")
  @NotBlank
  private String assetId;

  @Schema(description = "The owner of the asset")
  @Valid
  @NotNull
  private OrganizationDTO assetOwner;

  @Schema(description = "The status of the asset", example = "GRANTED")
  private AccessStatus accessStatus;

  @Schema(description = "The asset creation datetime (ISO8601 = yyyy-MM-dd'T'HH:mm:ss.SSSXXX)")
  @ISO8601JsonStringFormat
  private ZonedDateTime creationDatetime;

  @Schema(description = "The NFT operator")
  private OrganizationDTO operator;

  @Schema(description = "The Id of the multisig contracts", example = "2")
  private Integer multisigContractId;

  @Schema(description = "The Id of the NFT passport (null if it is not minted)", example = "2")
  private Integer tokenId;

  @Schema(description = "The token status of the the asset", example = "PENDING_CREATION")
  private TokenStatus tokenStatus;

  @JsonInclude(Include.NON_NULL)
  @Schema(description = "The number of GrantedAccess on this asset, only available for the asset provider", example = "12")
  private Long grantedAccess;


}
