package collaborate.api.datasource.servicedata.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.tag.model.user.UserWalletDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDataDetailsDTO {

  @Schema(description = "The asset catalog of datasources")
  private AssetDataCatalogDTO assetDataCatalog;

  @Schema(description = "The id of the asset", example = "5NPET4AC8AH593530")
  private String assetId;

  @Schema(description = "The owner of the asset")
  private UserWalletDTO assetOwner;

  @Schema(description = "The status of the the asset", example = "GRANTED")
  private AccessStatus accessStatus;

  @Schema(description = "The asset creation datetime (ISO8601 = yyyy-MM-dd'T'HH:mm:ss.SSSXXX)")
  @ISO8601JsonStringFormat
  private ZonedDateTime creationDatetime;

  @Schema(description = "The NFT operator")
  private OrganizationDTO operator;

  @Schema(description = "The Id of the multisig contracts", example = "2")
  private Integer multisigContractId;

  @Schema(description = "The Id of the NFT servicedata (null if it is not minted)", example = "2")
  private Integer tokenId;

  @Schema(description = "The token status of the the asset", example = "PENDING_CREATION")
  private TokenStatus tokenStatus;

  @JsonIgnore
  public long countScopes() {
    return getAssetDataCatalog()
        .getDatasources()
        .stream()
        .map(AssetDetailsDatasourceDTO::getScopes)
        .mapToLong(Set::size)
        .sum();
  }

}
