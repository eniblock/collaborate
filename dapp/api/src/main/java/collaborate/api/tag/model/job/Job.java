package collaborate.api.tag.model.job;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Job {

  @Getter
  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
  public enum Status {
    @JsonProperty("created")
    CREATED,
    @JsonProperty("forged")
    FORGED,
    @JsonProperty("published")
    PUBLISHED;
  }

  @Schema(description = "The job identifier", example = "374")
  private Integer id;
  @Schema(description = "The execution status of this job", example = "created")
  private Status status;
  // TODO: openapi description
  @JsonProperty("raw_transaction")
  private String rawTransaction;
  // TODO: openapi description
  @JsonProperty("operation_hash")
  private String operationHash;
  // TODO: openapi description
  @JsonProperty("error_message")
  private String errorMessage;

}
