package collaborate.api.datasource.kpi.find;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringCriteriaConditionBuilder implements ConditionCriteriaBuilder<String> {

  private Predicate predicate;
  private CriteriaBuilder builder;
  private Root<?> root;

  @Override
  public void handleGreaterThan(Expression<String> field, String value) {
    predicate = builder.and(
        predicate,
        builder.greaterThanOrEqualTo(field, value));
  }

  @Override
  public void handleLowerThan(Expression<String> field, String value) {
    predicate = builder.and(
        predicate,
        builder.lessThanOrEqualTo(field, value));
  }

  @Override
  public void handleContains(Expression<String> field, String value) {
    predicate = builder.and(
        predicate,
        builder.like(field, "%" + value + "%"));
  }

  @Override
  public void handleEqual(Expression<String> field, String value) {
    predicate = builder.and(
        predicate,
        builder.equal(field, value));
  }
}
