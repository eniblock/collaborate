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
    private URI url;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DatasourceAccessMethod accessMethod;

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

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public DatasourceAccessMethod getAccessMethod() {
        return accessMethod;
    }

    public void setAccessMethod(DatasourceAccessMethod accessMethod) {
        this.accessMethod = accessMethod;
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
