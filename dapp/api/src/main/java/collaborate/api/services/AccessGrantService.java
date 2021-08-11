package collaborate.api.services;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.domain.AccessGrantParams;
import collaborate.api.domain.Transaction;
import collaborate.api.domain.TransactionBatch;
import collaborate.api.restclient.ITezosApiGatewayClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AccessGrantService {
    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private ITezosApiGatewayClient tezosApiGatewayClient;

    TransactionBatch<AccessGrantParams> transactionBatch = new TransactionBatch<AccessGrantParams>();

    public void addAccessGrant(AccessGrantParams params) {
        Transaction<AccessGrantParams> transaction = new Transaction<>();

        transaction.setContractAddress(apiProperties.getContractAddress());
        transaction.setEntryPoint("grantAccess");
        transaction.setEntryPointParams(params);

        transactionBatch.getTransactions().add(transaction);
    }

    @Scheduled(cron = "*/20 * * * * *")
    public void test() {

        List<Transaction<AccessGrantParams>> transactions = transactionBatch.getTransactions();
        if(transactions.size() == 0) {
            return;
        }

        transactionBatch.setSecureKeyName(apiProperties.getOrganizationId());
        tezosApiGatewayClient.sendTransactionBatch(transactionBatch);

        transactionBatch = new TransactionBatch<AccessGrantParams>();
    }

}
