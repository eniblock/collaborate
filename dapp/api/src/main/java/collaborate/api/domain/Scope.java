package collaborate.api.domain;

import collaborate.api.domain.enumeration.ScopeStatus;

import java.util.UUID;

public class Scope {
    private String organizationId;
    private String organizationName;
    private Long datasourceId;
    private String scope;
    private UUID scopeId;
    private ScopeStatus status;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public UUID getScopeId() {
        return scopeId;
    }

    public void setScopeId(UUID scopeId) {
        this.scopeId = scopeId;
    }

    public ScopeStatus getStatus() {
        return status;
    }

    public void setStatus(ScopeStatus status) {
        this.status = status;
    }
}
