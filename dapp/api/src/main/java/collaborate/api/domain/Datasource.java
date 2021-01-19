package collaborate.api.domain;

import collaborate.api.domain.enumeration.DatasourceAccessMethod;
import collaborate.api.domain.enumeration.DatasourceTransferMethod;
import collaborate.api.domain.enumeration.DatasourceType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Entity
public class Datasource {

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
    private String clientId;

    @NotNull
    private String clientSecret;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasourceTransferMethod transferMethod;

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public DatasourceTransferMethod getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(DatasourceTransferMethod transferMethod) {
        this.transferMethod = transferMethod;
    }
}
