package collaborate.api.domain;

import collaborate.api.domain.enumeration.ScopeStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Document implements Serializable {
    String id;
    String organizationId;
    String organizationName;
    Long datasourceId;
    String documentId;
    String title;
    String scope;
    UUID scopeId;
    String type;
    Date synchronizedAt;
    ScopeStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getSynchronizedAt() {
        return synchronizedAt;
    }

    public void setSynchronizedAt(Date synchronizedAt) {
        this.synchronizedAt = synchronizedAt;
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

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", datasourceId=" + datasourceId +
                ", documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", scope='" + scope + '\'' +
                ", scopeId=" + scopeId +
                ", type='" + type + '\'' +
                ", synchronizedAt=" + synchronizedAt +
                ", status=" + status +
                '}';
    }
}
