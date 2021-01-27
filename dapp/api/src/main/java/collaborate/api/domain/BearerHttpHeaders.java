package collaborate.api.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class BearerHttpHeaders extends HttpHeaders {
    public BearerHttpHeaders(AccessTokenResponse accessTokenResponse) {
        super();

        this.setContentType(MediaType.APPLICATION_JSON);
        this.set("Authorization", "Bearer " + accessTokenResponse.getAccessToken());
    }
}
