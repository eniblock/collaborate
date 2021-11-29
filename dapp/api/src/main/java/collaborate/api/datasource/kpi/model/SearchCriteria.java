package collaborate.api.datasource.kpi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  private String key;
  private String operation;
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
