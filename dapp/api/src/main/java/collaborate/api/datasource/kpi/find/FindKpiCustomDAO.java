package collaborate.api.datasource.kpi.find;

import collaborate.api.datasource.kpi.Kpi;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class FindKpiCustomDAO {

  @PersistenceContext
  private EntityManager entityManager;

  public List<KpiAggregation> find(KpiQuery params) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<KpiAggregation> query = builder.createQuery(KpiAggregation.class);
    Root<Kpi> kpiRoot = query.from(Kpi.class);

    Expression<String> extractDate = buildExtractDateExpression(
        params.getLabelGroup(),
        params.getLabelFormat(),
        kpiRoot);

    query = query
        .multiselect(
            kpiRoot.get(params.getDataSetsGroup()).alias("dataSetGroup"),
            extractDate.alias("label"),
            builder.count(kpiRoot).alias("total")
        )
        .where(buildPredicate(params.getSearch(), builder, kpiRoot))
        .groupBy(kpiRoot.get(params.getDataSetsGroup()), extractDate);

    return entityManager.createQuery(query).getResultList();
  }

  private Expression<String> buildExtractDateExpression(String dateField, String dateFormat,
      Root<Kpi> kpiRoot) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    return builder.function(
        "to_char",
        String.class,
        kpiRoot.get(dateField),
        builder.literal(dateFormat));
  }

  private Predicate buildPredicate(Collection<SearchCriteria> search, CriteriaBuilder builder,
      Root<Kpi> kpiRoot) {
    Predicate predicate = builder.conjunction();
    SearchCriteriaConsumer searchConsumer = new SearchCriteriaConsumer(
        predicate,
        builder,
        kpiRoot);
    search.forEach(searchConsumer);
    return searchConsumer.getPredicate();
  }

}
