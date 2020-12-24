package collaborate.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {
    private String platform;
    private String idpAdminRole;

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
}
