package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

public class AccessGrantParams implements Serializable {
    @NotNull
    private UUID id;

    @NotNull
    private String requesterAddress;

    @NotNull
    private String providerAddress;

    @NotNull
    private String jwtToken;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getRequesterAddress() {
        return requesterAddress;
    }

    public void setRequesterAddress(String requesterAddress) {
        this.requesterAddress = requesterAddress;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    @Override
    public String toString() {
        return "AccessGrantParams{" +
                "id=" + id +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
