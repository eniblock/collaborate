package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.kpi.JsonSpecification;
import collaborate.api.datasource.kpi.JsonSpecificationConsumer;
import collaborate.api.datasource.model.Nft;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
@AllArgsConstructor
public class NftSpecification implements Specification<Nft> {

  private List<JsonSpecification> specifications;

  /**
   * Build a jsonSpecification based on the provided jsonSpecs
   *
   * @param jsonSpecs a map where key is a jsonPath and value is the searched value
   */
  public NftSpecification(Map<String, String> jsonSpecs) {
    if (jsonSpecs.isEmpty()) {
      this.specifications = new ArrayList<>();
    } else {
      this.specifications = jsonSpecs.entrySet().stream()
          .map(entry -> new JsonSpecification(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList());
    }
  }

  @Override
  public Predicate toPredicate(Root<Nft> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate = builder.conjunction();
    var jsonSpecConsumer = new JsonSpecificationConsumer(predicate, builder, root);
    specifications.forEach(jsonSpecConsumer);
    return jsonSpecConsumer.getPredicate();
  }
}