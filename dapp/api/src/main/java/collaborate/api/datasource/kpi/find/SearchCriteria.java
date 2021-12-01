package collaborate.api.datasource.kpi.find;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

  public static final String OPERATION_EQ = "=";
  public static final String OPERATION_CONTAINS = ":";
  public static final String OPERATION_LT = "<";
  public static final String OPERATION_GT = ">";

  @Schema(description = "The field the predicate is apply on",
      example = "kpiKey",
      required = true)
  @NotEmpty
  private String field;

  @Schema(description = "The kind of predicate operation", example = "=", required = true)
  @NotEmpty
  private String operation;

  @Schema(description = "The predicate value", example = "5", required = true)
  @NotEmpty
  private Object value;

  @JsonIgnore
  public boolean isOperationGreaterThan() {
    return operation.equals(OPERATION_GT);
  }

  @JsonIgnore
  public boolean isOperationLowerThan() {
    return operation.equals(OPERATION_LT);
  }

  @JsonIgnore
  public boolean isOperationLike() {
    return operation.equals(OPERATION_CONTAINS);
  }

  @JsonIgnore
  public boolean isOperationEqual() {
    return operation.equals(OPERATION_EQ);
  }
}
