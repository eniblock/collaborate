package collaborate.api.organization.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDTO {

  @Schema(description = "The name of the organization.", example = "PSA", required = true)
  private String name;
  @Schema(description = "The hash value of the publicKey", example = "edpkv2qoSugVizsZRt9dCb2v4iizRhZEQkw2PF5JyZUgHyE6Bp9Yv2")
  private String publicKeyHash;
  @Schema(description = "The publicKey key of the organization", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  private String publicKey;

}