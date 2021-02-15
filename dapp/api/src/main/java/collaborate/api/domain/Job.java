package collaborate.api.domain;

public class Job {
    enum Status {
        created,
    }

    private Integer id;
    private Status status;
    private String rawTransaction;
    private String operationHash;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTransaction(String rawTransaction) {
        this.rawTransaction = rawTransaction;
    }

    public String getOperationHash() {
        return operationHash;
    }

    public void setOperationHash(String operationHash) {
        this.operationHash = operationHash;
    }
}
