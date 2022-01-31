package collaborate.api.datasource.multisig;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransactionPK;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProxyTokenControllerTransactionDAO extends
    JpaRepository<ProxyTokenControllerTransaction, ProxyTokenControllerTransactionPK> {

  Optional<ProxyTokenControllerTransaction> findBySmartContractAndMultiSigId(String smartContract,
      Long multiSigId);

  List<ProxyTokenControllerTransaction> findAllBySmartContractAndEntrypointAndIsSignedAndOwner(
      String smartContract, String entrypoint, Boolean isSigned, String owner);

  List<ProxyTokenControllerTransaction> findAllBySmartContractAndEntrypointAndIsSignedAndOperator(
      String smartContract, String entrypoint, Boolean isSigned, String operator);
}
