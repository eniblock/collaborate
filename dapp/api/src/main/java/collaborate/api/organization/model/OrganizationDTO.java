package collaborate.api.organization.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class OrganizationDTO {

  @Schema(description = "The name of the organization.", example = "DSPConsortium1", required = true)
  @NotBlank
  @LegalNameNotUsedConstraint
  private String legalName;

  @Schema(description = "The wallet address", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  @NotBlank
  @AddressNotUsedConstraint
  private String address;

  @Schema(description = "The publicKey key of the organization", example = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm97j68ewFohnhMgcPNo32iZGhbyKOv/W6Q61Z54DQtRicY9+KeOFsgmn0PUtiP3NK9UWhCx7OgGl3/9d7TG5Y1vC2pobrPPmJrZfRfPCIyXO/U7f5BvRn0vudKR1cgQY2rOFIXc1a51uDhQe2f71lkHWOEBculN2VaMbcEIyIKS59S0nePF0/Qb6z1B7tIGhdNN4MIu8QDL1FaYCO+vcNNgPSuFViIO/u13yhY2n7Jf5yNPgqZi9NOdNlgVfosl62PfNBHDW4U2hu04CxKr4e5AainmbOP7mxgX6Iuk8zFiASlESwNqjKWQJSR4HXAf8TIe1FdNexoaeqMm0ajLCcwIDAQAB")
  @Size(min = 392, max = 392, message = "The RSA public key length should be 392 characters long")
  private String encryptionKey;

  @Schema(description = "The roles of the organization", example = "[1,2]")
  @NotEmpty
  private List<OrganizationRole> roles;

  @Schema(description = "The organization status", example = "0")
  private OrganizationStatus status = OrganizationStatus.ACTIVE;
}
