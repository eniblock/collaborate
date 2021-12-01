package collaborate.api.datasource.kpi.find;

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
public class SearchCriteriaConsumer implements Consumer<SearchCriteria> {

  public static final String JSON_FIELD_SEPARATOR = ".";
  private Predicate predicate;
  private CriteriaBuilder builder;
  private Root<?> root;

  @Override
  public void accept(SearchCriteria search) {
    if (!StringUtils.contains(search.getField(), JSON_FIELD_SEPARATOR)
        && root.get(search.getField()).getJavaType() == ZonedDateTime.class) {
      var consumer = new ZonedDateTimeCriteriaConditionBuilder(predicate, builder, root);
      consumer.consume(
          search,
          root.get(search.getField()),
          ZonedDateTime.parse(search.getValue().toString())
      );
      predicate = consumer.getPredicate();
    } else {
      var consumer = new StringCriteriaConditionBuilder(predicate, builder, root);
      consumer.consume(
          search,
          buildFieldExpression(search.getField()),
          search.getValue().toString()
      );
      predicate = consumer.getPredicate();
    }
  }

  Expression<String> buildFieldExpression(String field) {
    if (StringUtils.contains(field, JSON_FIELD_SEPARATOR)) {
      var jsonBlobField = StringUtils.substringBefore(field, JSON_FIELD_SEPARATOR);
      var jsonPath = StringUtils.substringAfter(field, JSON_FIELD_SEPARATOR);
      String[] jsonPaths;
      if (StringUtils.contains(jsonPath, JSON_FIELD_SEPARATOR)) {
        jsonPaths = StringUtils.split(jsonPath, JSON_FIELD_SEPARATOR);
      } else {
        jsonPaths = new String[]{jsonPath};
      }

      List<Expression<String>> extractPathParams = new ArrayList<>();
      extractPathParams.add(root.get(jsonBlobField));
      Arrays.stream(jsonPaths).map(s -> builder.literal(s)).forEach(extractPathParams::add);
      return builder.function("jsonb_extract_path_text",
          String.class, extractPathParams.toArray(new Expression[0])
      );
    } else {
      return root.get(field);
    }
  }

}
