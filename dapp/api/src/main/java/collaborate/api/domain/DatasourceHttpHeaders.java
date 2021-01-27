package collaborate.api.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class DatasourceHttpHeaders extends HttpHeaders {
    public DatasourceHttpHeaders(AccessTokenResponse accessTokenResponse) {
        this.setContentType(MediaType.APPLICATION_JSON);
        this.set("Authorization", "Bearer " + accessTokenResponse.getAccessToken());
    }
}
