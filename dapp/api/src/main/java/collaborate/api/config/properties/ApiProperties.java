package collaborate.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {
    private String platform;
    private String idpAdminRole;
    private String organizationId;
    private String organizationName;
    private String contractAddress;

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
}
