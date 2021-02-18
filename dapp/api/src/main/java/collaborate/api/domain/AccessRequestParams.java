package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

public class AccessRequestParams implements Serializable {
    @NotNull
    private UUID id;

    @NotNull
    private Long datasourceId;

    @NotNull
    private UUID scopeId;

    @NotNull
    private String requesterAddress;

    @NotNull
    private String providerAddress;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public UUID getScopeId() {
        return scopeId;
    }

    public void setScopeId(UUID scopeId) {
        this.scopeId = scopeId;
    }

    public String getRequesterAddress() {
        return requesterAddress;
    }

    public void setRequesterAddress(String requesterAddress) {
        this.requesterAddress = requesterAddress;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }
}
