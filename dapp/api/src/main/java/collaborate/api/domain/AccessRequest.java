package collaborate.api.domain;

import collaborate.api.domain.enumeration.AccessRequestStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class AccessRequest {
    @Id
    private UUID id;
    private Long datasourceId;
    private UUID scopeId;
    @Enumerated(EnumType.STRING)
    private AccessRequestStatus status = AccessRequestStatus.REQUESTED;
    private String requesterAddress;
    private String providerAddress;
    private Timestamp createdAt;
    private String jwtToken;

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

    public AccessRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AccessRequestStatus status) {
        this.status = status;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public String toString() {
        return "AccessRequest{" +
                "id=" + id +
                ", datasourceId=" + datasourceId +
                ", scopeId=" + scopeId +
                ", status=" + status +
                ", requesterAddress='" + requesterAddress + '\'' +
                ", providerAddress='" + providerAddress + '\'' +
                ", createdAt=" + createdAt +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
