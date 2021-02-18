package collaborate.api.domain;

import java.util.ArrayList;
import java.util.List;

public class TransactionBatch<T> {
    private List<Transaction<T>> transactions = new ArrayList<>();
    private String secureKeyName;

    public List<Transaction<T>> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction<T>> transactions) {
        this.transactions = transactions;
    }

    public String getSecureKeyName() {
        return secureKeyName;
    }

    public void setSecureKeyName(String secureKeyName) {
        this.secureKeyName = secureKeyName;
    }

    @Override
    public String toString() {
        return "TransactionBatch{" +
                "transactions=" + transactions +
                ", secureKeyName='" + secureKeyName + '\'' +
                '}';
    }
}
