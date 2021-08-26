package collaborate.api.tag.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  private Integer id;
  private Status status;
  private String rawTransaction;
  private String operationHash;

}
