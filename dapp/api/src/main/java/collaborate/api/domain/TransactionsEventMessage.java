package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TransactionsEventMessage<T> implements Serializable {
    @NotNull
    private String entrypoint;

    @NotNull
    private String contractAddress;

    @NotNull
    private TransactionsEventParameters<T> parameters;

    public String getEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(String entrypoint) {
        this.entrypoint = entrypoint;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public TransactionsEventParameters getParameters() {
        return parameters;
    }

    public void setParameters(TransactionsEventParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "TransactionsEventMessage{" +
                "entrypoint='" + entrypoint + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
