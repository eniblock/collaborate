package collaborate.api.businessdata;

import collaborate.api.businessdata.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDAO extends JpaRepository<TransactionEntity, Long> {

}
