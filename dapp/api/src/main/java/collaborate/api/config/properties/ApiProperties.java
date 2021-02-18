package collaborate.api.config.properties;

import collaborate.api.domain.Organization;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {
    private String platform;
    private String idpAdminRole;
    private String organizationId;
    private String organizationName;
    private String contractAddress;
    private HashMap<String, Organization> organizations;
    private String catalogApiUrl;
    private String tezosApiGatewayUrl;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIdpAdminRole() {
        return idpAdminRole;
    }

    public void setIdpAdminRole(String idpAdminRole) {
        this.idpAdminRole = idpAdminRole;
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

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public HashMap<String, Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(HashMap<String, Organization> organizations) {
        this.organizations = organizations;
    }

    public String getCatalogApiUrl() {
        return catalogApiUrl;
    }

    public void setCatalogApiUrl(String catalogApiUrl) {
        this.catalogApiUrl = catalogApiUrl;
    }

    public String getTezosApiGatewayUrl() {
        return tezosApiGatewayUrl;
    }

    public void setTezosApiGatewayUrl(String tezosApiGatewayUrl) {
        this.tezosApiGatewayUrl = tezosApiGatewayUrl;
    }
}
