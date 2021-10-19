package collaborate.api.datasource.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.validation.constraints.NotNull;
import collaborate.api.config.ISO8601JsonStringFormat;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Datasource {

  public String type;
  @Schema(description = "The unique identifier of the datasource", example = "5NPET4AC8AH593530", required = true)
  @NotNull
  private String id;
  @Schema(description = "The datasource label", example = "DSPName - Vehicles", required = true)
  @NotNull
  private String name;
  @Schema(description = "The provider class used to configure the datasource access", example = "collaborate.api.datasource.provider.TraefikConfiguration", required = true)
  private String provider;
  @Schema(description = "Addition data about the datasource usage and configuration", required = true)
  private Set<Attribute> providerMetadata;
  @Schema(description = "The datasource provider configuration", required = true)
  private LinkedHashMap<?, ?> providerConfiguration;
  @ISO8601JsonStringFormat
  private ZonedDateTime creationDatetime;
}
