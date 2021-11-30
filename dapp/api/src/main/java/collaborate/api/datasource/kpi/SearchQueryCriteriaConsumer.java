package collaborate.api.datasource.kpi;

import collaborate.api.datasource.kpi.model.SearchCriteria;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {

  private Predicate predicate;
  private CriteriaBuilder builder;
  private Root<?> root;

  @Override
  public void accept(SearchCriteria search) {
    if (search.isOperationGreaterThan()) {
      predicate = builder.and(
          predicate,
          builder.greaterThanOrEqualTo(buildFieldExpression(search.getKey()),
              search.getValue().toString())
      );
    } else if (search.isOperationLowerThan()) {
      predicate = builder.and(
          predicate,
          builder.lessThanOrEqualTo(buildFieldExpression(search.getKey()),
              search.getValue().toString())
      );
    } else if (search.isOperationLike()) {
      if (StringUtils.contains(search.getKey(), ".")
          || root.get(search.getKey()).getJavaType() == String.class) {
        predicate = builder.and(
            predicate,
            builder.like(buildFieldExpression(search.getKey()), "%" + search.getValue() + "%")
        );
      } else {
        predicate = builder.and(
            predicate,
            builder.equal(buildFieldExpression(search.getKey()), search.getValue())
        );
      }
    } else if (search.isOperationEqual()) {
      predicate = builder.and(
          predicate,
          builder.equal(buildFieldExpression(search.getKey()), search.getValue())
      );
    }
  }

  Expression<String> buildFieldExpression(String field) {
    if (StringUtils.contains(field, ".")) {
      var jsonBlobField = StringUtils.substringBefore(field, ".");
      var jsonPath = StringUtils.substringAfter(field, ".");
      String[] jsonPaths;
      if (StringUtils.contains(jsonPath, ".")) {
        jsonPaths = StringUtils.split(jsonPath, ".");
      } else {
        jsonPaths = new String[]{jsonPath};
      }

      List<Expression<String>> extractPathParams = new ArrayList<>();
      extractPathParams.add(root.get(jsonBlobField));
      Arrays.stream(jsonPaths).map(s -> builder.literal(s)).forEach(extractPathParams::add);
      return builder.function("jsonb_extract_path_text",
          String.class, extractPathParams.toArray(new Expression[extractPathParams.size()])
      );
    } else {
      return root.get(field);
    }
  }

  Object buildSearchValueForField(String field, Object value) {
    if (root.get(field).getJavaType() == ZonedDateTime.class) {
      return ZonedDateTime.parse(value.toString());
    }
    return value.toString();
  }
}
