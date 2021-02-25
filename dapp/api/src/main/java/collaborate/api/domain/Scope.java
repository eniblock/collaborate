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

    public void setStatusFromAccessRequest(AccessRequest accessRequest) {
        if (accessRequest != null) {
            switch (accessRequest.getStatus()) {
                case REQUESTED:
                    this.setStatus(ScopeStatus.PENDING);
                    break;
                case REVOKED:
                case REJECTED:
                    this.setStatus(ScopeStatus.LOCKED);
                    break;
                case GRANTED:
                    this.setStatus(ScopeStatus.GRANTED);
                    break;
            }
        } else {
            this.setStatus(ScopeStatus.LOCKED);
        }
    }

    public static Scope createFromDocument(Document document) {
        Scope scope = new Scope();

        scope.setOrganizationId(document.getOrganizationId());
        scope.setOrganizationName(document.getOrganizationName());
        scope.setScope(document.getScope());
        scope.setScopeId(document.getScopeId());
        scope.setDatasourceId(document.getDatasourceId());

        return scope;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "organizationId='" + organizationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", datasourceId=" + datasourceId +
                ", scope='" + scope + '\'' +
                ", scopeId=" + scopeId +
                ", status=" + status +
                '}';
    }
}
