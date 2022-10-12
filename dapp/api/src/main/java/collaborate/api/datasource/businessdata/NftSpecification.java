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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
@AllArgsConstructor
public class NftSpecification implements Specification<Nft> {

  public static final String OWNER_ADDRESS = "ownerAddress";
  @Getter(value = AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private List<JsonSpecification> specifications;
  private String eqOwnerAddress;
  private String notEqOwnerAddress;

  /**
   * Build a jsonSpecification based on the provided metadataSpecs
   *
   * @param metadataSpecs a map where key is a jsonPath and value is the searched value
   */
  public NftSpecification(Map<String, String> metadataSpecs) {
    setMetadata(metadataSpecs);
  }

  @Override
  public Predicate toPredicate(Root<Nft> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate = builder.conjunction();
    var jsonSpecConsumer = new JsonSpecificationConsumer("metadata", predicate, builder, root);
    specifications.forEach(jsonSpecConsumer);

    predicate = jsonSpecConsumer.getPredicate();
    if (StringUtils.isNotBlank(eqOwnerAddress)) {
      predicate = builder.and(
          builder.equal(
              root.get(OWNER_ADDRESS),
              eqOwnerAddress),
          predicate);
    }
    if (StringUtils.isNotBlank(notEqOwnerAddress)) {
      predicate = builder.and(
          builder.or(
              builder.notEqual(
                  root.get(OWNER_ADDRESS),
                  notEqOwnerAddress),
              builder.isNull(root.get(OWNER_ADDRESS))
          ),
          predicate
      );
    }

    return predicate;
  }

  public void setMetadata(Map<String, String> filters) {
    if (filters == null) {
      this.specifications = new ArrayList<>();
    } else {
      this.specifications = filters.entrySet().stream()
          .map(entry -> new JsonSpecification(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList());
    }
  }
}
