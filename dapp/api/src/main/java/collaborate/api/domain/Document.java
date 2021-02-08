package collaborate.api.domain;

import java.io.Serializable;
import java.util.UUID;

public class Document implements Serializable {
    String organizationId;
    String organizationName;
    Long datasourceId;
    String documentId;
    String title;
    String scope;
    UUID scopeId;
    String type;

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

    @Override
    public String toString() {
        return "Document{" +
                "organizationId='" + organizationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", datasourceId=" + datasourceId +
                ", documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", scope='" + scope + '\'' +
                ", scopeId=" + scopeId +
                ", type='" + type + '\'' +
                '}';
    }
}
