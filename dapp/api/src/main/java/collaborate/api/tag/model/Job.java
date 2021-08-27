package collaborate.api.tag.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Data
public class Job {

  @Getter
  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
  public enum Status {
    @JsonProperty("created")
    CREATED;
  }

  @Schema(description = "The job identifier", example = "374")
  private Integer id;
  @Schema(description = "The execution status of this job", example = "created")
  private Status status;
  // TODO: openapi description
  private String rawTransaction;
  // TODO: openapi description
  private String operationHash;

}
