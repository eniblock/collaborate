package collaborate.api.datasource.multisig;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransactionPK;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProxyTokenControllerTransactionDAO extends JpaRepository<ProxyTokenControllerTransaction, ProxyTokenControllerTransactionPK> {

  Optional<ProxyTokenControllerTransaction> findBySmartContractAndMultiSigId(String smartContract, Long multiSigId);

  List<ProxyTokenControllerTransaction> findAllBySmartContractAndEntrypointAndOwner(String smartContract, String entrypoint, String owner);

  List<ProxyTokenControllerTransaction> findAllBySmartContractAndEntrypointAndOperator(String smartContract, String entrypoint, String operator);
}
