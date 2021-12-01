package collaborate.api.datasource.kpi.find;

import javax.persistence.criteria.Expression;

public interface ConditionCriteriaBuilder<T extends Comparable<? super T>> {

  default void consume(
      SearchCriteria search,
      Expression<T> field,
      T value
  ) {
    if (search.isOperationGreaterThan()) {
      handleGreaterThan(field, value);
    } else if (search.isOperationLowerThan()) {
      handleLowerThan(field, value);
    } else if (search.isOperationLike()) {
      handleContains(field, value);
    } else if (search.isOperationEqual()) {
      handleEqual(field, value);
    }
  }

  void handleGreaterThan(Expression<T> field,
      T value);

  void handleLowerThan(Expression<T> field,
      T value);

  void handleContains(Expression<T> field,
      T value);

  void handleEqual(Expression<T> field,
      T value);
}
