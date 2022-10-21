package collaborate.api.datasource.servicedata.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceData {

  @Schema(description = "The unique identifier of the servicedata", example = "c36f12b9-d98c-4450-8fb8-93960466b45d", required = true)
  @NotNull
  @Id
  private String id;

  @JsonIgnore
  private String cid;

  @Schema(description = "The servicedata label", example = "DSPName - Vehicles", required = true)
  @NotNull
  private String name;

  @Schema(description = "The servicedata desc", example = "DSPName", required = true)
  @NotNull
  private String description;

  @Schema(description = "The servicedata owner wallet address", example = "tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG", required = true)
  @NotNull
  private String owner;

  @Schema(description = "The provider class used to configure the servicedata access", example = "collaborate.api.datasource.gateway.datasource.provider.TraefikConfiguration", required = true)
  private String provider;

  @ISO8601JsonStringFormat
  private ZonedDateTime creationDatetime;

/*
  @Schema(description = "Additional data about the servicedata usage and configuration", required = true)
  @ElementCollection(targetClass = Metadata.class)
  private Set<Metadata> providerMetadata;

  public Optional<String> findMetadataByName(String name) {
    if (providerMetadata == null) {
      return Optional.empty();
    }
    return providerMetadata.stream().filter(m -> StringUtils.equals(name, m.getName()))
        .map(Metadata::getValue).findFirst();
  }

  public List<String> getPurpose(ObjectMapper objectMapper) {
    var rawPurpose = findMetadataByName(DATASOURCE_PURPOSE).orElseThrow(
        () -> new IllegalStateException("No purpose found in datasourceId=" + getId()));
    try {
      return objectMapper.readValue(rawPurpose, new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
          "For datasourceId=" + getId() + ", can't deserialize purpose=" + rawPurpose);
    }
  }

  public String getType() {
    return findMetadataByName(DATASOURCE_TYPE).orElseThrow(
        () -> new IllegalStateException("No type for datasourceId=" + getId()));
  }
*/
}
