package collaborate.api.datasource.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Builder
public class DatasourceClientSecret implements Serializable {
    @Id
    private Long id;

    private String clientId;

    private String clientSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "DatasourceClientSecret{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                '}';
    }
}
