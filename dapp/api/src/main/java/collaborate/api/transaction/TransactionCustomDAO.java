package collaborate.api.transaction;

import collaborate.api.datasource.kpi.find.SearchCriteria;
import collaborate.api.datasource.kpi.find.SearchCriteriaConsumer;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionCustomDAO {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Transaction> find(Collection<SearchCriteria> searchCriteria) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Transaction> query = builder.createQuery(Transaction.class);
    Root<Transaction> transactionRoot = query.from(Transaction.class);

    query = query
        .select(
            transactionRoot
        )
        .where(buildPredicate(searchCriteria, builder, transactionRoot));

    return entityManager.createQuery(query).getResultList();
  }

  private <T> Predicate buildPredicate(Collection<SearchCriteria> search, CriteriaBuilder builder,
      Root<T> kpiRoot) {
    Predicate predicate = builder.conjunction();
    SearchCriteriaConsumer searchConsumer = new SearchCriteriaConsumer(
        predicate,
        builder,
        kpiRoot);
    search.forEach(searchConsumer);
    return searchConsumer.getPredicate();
  }

}
