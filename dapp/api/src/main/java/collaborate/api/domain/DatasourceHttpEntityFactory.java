package collaborate.api.domain;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class DatasourceHttpEntityFactory {
    public HttpEntity<Void> create(AccessTokenResponse accessTokenResponse) {
        HttpHeaders datasourceHeaders = new DatasourceHttpHeaders(accessTokenResponse);
        HttpEntity<Void> datasourceEntity = new HttpEntity<Void>(datasourceHeaders);

        return datasourceEntity;
    }
}
