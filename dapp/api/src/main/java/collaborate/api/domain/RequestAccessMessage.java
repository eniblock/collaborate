package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedHashMap;

public class RequestAccessMessage implements Serializable {
    @NotNull
    private String entrypoint;

    @NotNull
    private String contractAddress;

    @NotNull
    private RequestAccessParameters parameters;

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

    public RequestAccessParameters getParameters() {
        return parameters;
    }

    public void setParameters(RequestAccessParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "RequestAccessMessage{" +
                "entrypoint='" + entrypoint + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
