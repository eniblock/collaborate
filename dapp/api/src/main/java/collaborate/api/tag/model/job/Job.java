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

  @Getter
  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
  public enum Type {
    @JsonProperty("transaction")
    TRANSACTION,
    @JsonProperty("revealed")
    REVEALEAD
  }

  @Schema(description = "The job identifier", example = "374")
  private Integer id;
  @Schema(description = "The execution status of this job", example = "created")
  private Status status;
  @Schema(description = "the raw forged operation corresponding to the job")
  private String forgedOperation;
  @Schema(description = "The unique identifier of the transaction on the block chain", example = "opGz2Sg1QiXchJMyz9v1rV9VN7mQadmkL81KVwteANQWZpgV5ew")
  private String operationHash;
  @Schema(description = "Error message", example = "Bad Request")
  private String errorMessage;
  @Schema(description = "the operation type")
  private Type operationKind;

}
