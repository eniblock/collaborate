package collaborate.api.domain;

import collaborate.api.domain.enumeration.DatasourceAccessMethod;
import collaborate.api.domain.enumeration.DatasourceStatus;
import collaborate.api.domain.enumeration.DatasourceTransferMethod;
import collaborate.api.domain.enumeration.DatasourceType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;

@Entity
public class Datasource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasourceType type;

    @NotNull
    private URI apiURI;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasourceAccessMethod accessMethod;

    @NotNull
    private URI issuerIdentifierURI;

    @NotNull
    private String wellKnownURIPathSuffix;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasourceTransferMethod transferMethod;

    @Enumerated(EnumType.STRING)
    private DatasourceStatus status = DatasourceStatus.CREATED;

    private Integer documentCount;

    private Date synchronizedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatasourceType getType() {
        return type;
    }

    public void setType(DatasourceType type) {
        this.type = type;
    }

    public URI getApiURI() {
        return apiURI;
    }

    public void setApiURI(URI apiURI) {
        this.apiURI = apiURI;
    }

    public DatasourceAccessMethod getAccessMethod() {
        return accessMethod;
    }

    public void setAccessMethod(DatasourceAccessMethod accessMethod) {
        this.accessMethod = accessMethod;
    }

    public URI getIssuerIdentifierURI() {
        return issuerIdentifierURI;
    }

    public void setIssuerIdentifierURI(URI issuerIdentifierURI) {
        this.issuerIdentifierURI = issuerIdentifierURI;
    }

    public String getWellKnownURIPathSuffix() {
        return wellKnownURIPathSuffix;
    }

    public void setWellKnownURIPathSuffix(String wellKnownURIPathSuffix) {
        this.wellKnownURIPathSuffix = wellKnownURIPathSuffix;
    }

    public DatasourceTransferMethod getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(DatasourceTransferMethod transferMethod) {
        this.transferMethod = transferMethod;
    }

    public DatasourceStatus getStatus() {
        return status;
    }

    public void setStatus(DatasourceStatus status) {
        this.status = status;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }

    public Date getSynchronizedAt() {
        return synchronizedAt;
    }

    public void setSynchronizedAt(Date synchronizedAt) {
        this.synchronizedAt = synchronizedAt;
    }

    @Override
    public String toString() {
        return "Datasource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", apiURI=" + apiURI +
                ", accessMethod=" + accessMethod +
                ", issuerIdentifierURI=" + issuerIdentifierURI +
                ", wellKnownURIPathSuffix='" + wellKnownURIPathSuffix + '\'' +
                ", transferMethod=" + transferMethod +
                ", status=" + status +
                ", documentCount=" + documentCount +
                '}';
    }
}
