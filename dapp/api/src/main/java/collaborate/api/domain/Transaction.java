package collaborate.api.domain;

public class Transaction<T> {
    private String contractAddress;
    private String entryPoint;
    private T entryPointParams;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public T getEntryPointParams() {
        return entryPointParams;
    }

    public void setEntryPointParams(T entryPointParams) {
        this.entryPointParams = entryPointParams;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "contractAddress='" + contractAddress + '\'' +
                ", entryPoint='" + entryPoint + '\'' +
                ", entryPointParams=" + entryPointParams +
                '}';
    }
}
