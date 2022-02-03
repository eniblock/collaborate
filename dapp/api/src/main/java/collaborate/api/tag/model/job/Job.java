package collaborate.api.tag.model.job;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Job {

  @Getter
  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
  public enum Status {
    @JsonProperty("created")
    CREATED,
    @JsonProperty("forged")
    FORGED,
    @JsonProperty("published")
    PUBLISHED
  }

  @Schema(description = "The job identifier", example = "374")
  private Integer id;
  @Schema(description = "The execution status of this job", example = "created")
  private Status status;
  // TODO: openapi description
  private String forgedOperation;
  // TODO: openapi description
  private String operationHash;
  // TODO: openapi description
  private String errorMessage;
  // TODO: openapi description
  private String operationKind;

}
