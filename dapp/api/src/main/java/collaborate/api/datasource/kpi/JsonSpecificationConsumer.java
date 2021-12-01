package collaborate.api.datasource.kpi;

import static collaborate.api.datasource.kpi.find.SearchCriteriaConsumer.JSON_FIELD_SEPARATOR;

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
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class JsonSpecificationConsumer implements Consumer<JsonSpecification> {

  private Predicate predicate;
  private CriteriaBuilder builder;
  private Root<?> root;

  @Override
  public void accept(JsonSpecification jsonSpecification) {
    String jsonPath = jsonSpecification.getJsonPath();
    String[] jsonPaths;
    if (StringUtils.contains(jsonPath, JSON_FIELD_SEPARATOR)) {
      jsonPaths = StringUtils.split(jsonPath, JSON_FIELD_SEPARATOR);
    } else {
      jsonPaths = new String[]{jsonPath};
    }

    List<Expression<String>> extractPathParams = new ArrayList<>();
    extractPathParams.add(root.get("values"));
    Arrays.stream(jsonPaths).map(s -> builder.literal(s)).forEach(extractPathParams::add);
    predicate =
        builder.and(
            predicate,
            builder.equal(
                builder.function("jsonb_extract_path_text",
                    String.class, extractPathParams.toArray(new Expression[0])
                ), jsonSpecification.getSearchedValue()
            )
        );
  }
}
