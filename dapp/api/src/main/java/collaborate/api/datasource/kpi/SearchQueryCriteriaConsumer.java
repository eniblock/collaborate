package collaborate.api.datasource.kpi;

import collaborate.api.datasource.kpi.model.SearchCriteria;
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
          builder.greaterThanOrEqualTo(root.get(search.getKey()), search.getValue().toString())
      );
    } else if (search.isOperationLowerThan()) {
      predicate = builder.and(
          predicate,
          builder.lessThanOrEqualTo(root.get(search.getKey()), search.getValue().toString())
      );
    } else if (search.isOperationLike()) {
      if (root.get(search.getKey()).getJavaType() == String.class) {
        predicate = builder.and(
            predicate,
            builder.like(root.get(search.getKey()), "%" + search.getValue() + "%")
        );
      } else {
        predicate = builder.and(
            predicate,
            builder.equal(root.get(search.getKey()), search.getValue())
        );
      }
    } else if (search.isOperationEqual()) {
      predicate = builder.and(
          predicate,
          builder.equal(root.get(search.getKey()), search.getValue())
      );
    }
    Expression<String> extractJsonPath = builder.function("jsonb_extract_path_text",
        String.class, root.get("parameters"), builder.literal("role")
    );
    predicate = builder.and(
        predicate,
        builder.like(extractJsonPath, "%" + "1" + "%")
    );
  }

  Expression<String> jsonBlob(String field) {
    if (StringUtils.contains(field, ".")) {
      var jsonBlobField = StringUtils.substringBefore(field, ".");
      var jsonPaths = StringUtils.substringAfter(field, ".").split(".");

      List<Expression<String>> extractPathParams = Arrays.asList(root.get(jsonBlobField));
      Arrays.stream(jsonPaths).map(s -> builder.literal(s)).forEach(extractPathParams::add);
      return builder.function("jsonb_extract_path_text",
          String.class, extractPathParams.toArray(new Expression[extractPathParams.size()])
      );
    } else {
      return root.get(field);
    }
  }
}
