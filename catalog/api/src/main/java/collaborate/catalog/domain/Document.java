package collaborate.catalog.domain;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@org.springframework.data.mongodb.core.mapping.Document
public class Document implements Serializable {
    @Id
    String id;
    String organizationId;
    String organizationName;
    Long datasourceId;
    String documentId;
    URI documentUri;
    String title;
    String scope;
    UUID scopeId;
    String type;
    Date synchronizedAt;

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

    public URI getDocumentUri() {
        return documentUri;
    }

    public void setDocumentUri(URI documentUri) {
        this.documentUri = documentUri;
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

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", datasourceId=" + datasourceId +
                ", documentId='" + documentId + '\'' +
                ", documentUri=" + documentUri +
                ", title='" + title + '\'' +
                ", scope='" + scope + '\'' +
                ", scopeId=" + scopeId +
                ", type='" + type + '\'' +
                ", synchronizedAt=" + synchronizedAt +
                '}';
    }
}
