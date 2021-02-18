package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.*;
import collaborate.api.domain.enumeration.AccessRequestStatus;
import collaborate.api.domain.enumeration.DatasourceStatus;
import collaborate.api.restclient.ITezosApiGatewayClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
