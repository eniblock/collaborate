package collaborate.api.datasource.kpi;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
@AllArgsConstructor
public class KpiSpecification implements Specification<Kpi> {

  private List<JsonSpecification> specifications;

  public KpiSpecification(String path, String value) {
    this.specifications = new ArrayList<>();
    this.specifications.add(new JsonSpecification(path, value));
  }

  @Override
  public Predicate toPredicate(Root<Kpi> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate = builder.conjunction();
    var jsonSpecConsumer = new JsonSpecificationConsumer(predicate, builder, root);
    specifications.forEach(jsonSpecConsumer);
    return jsonSpecConsumer.getPredicate();
  }
}
