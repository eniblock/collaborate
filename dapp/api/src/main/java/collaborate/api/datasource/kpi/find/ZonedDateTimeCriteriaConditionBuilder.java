package collaborate.api.datasource.kpi.find;

import static collaborate.api.datasource.kpi.find.SearchCriteria.OPERATION_CONTAINS;

import java.time.ZonedDateTime;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonedDateTimeCriteriaConditionBuilder implements
    ConditionCriteriaBuilder<ZonedDateTime> {

  private Predicate predicate;
  private CriteriaBuilder builder;
  private Root<?> root;


  @Override
  public void handleGreaterThan(Expression<ZonedDateTime> field, ZonedDateTime value) {
    predicate = builder.and(
        predicate,
        builder.greaterThanOrEqualTo(field, value));
  }

  @Override
  public void handleLowerThan(Expression<ZonedDateTime> field, ZonedDateTime value) {
    predicate = builder.and(
        predicate,
        builder.lessThanOrEqualTo(field, value));
  }

  @Override
  public void handleContains(Expression<ZonedDateTime> field, ZonedDateTime value) {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "opration \"" + OPERATION_CONTAINS + "\" is not supported"
    );
  }

  @Override
  public void handleEqual(Expression<ZonedDateTime> field, ZonedDateTime value) {
    predicate = builder.and(
        predicate,
        builder.equal(field, value));
  }
}
