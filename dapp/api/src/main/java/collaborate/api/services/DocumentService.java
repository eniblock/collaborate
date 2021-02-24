package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.Document;
import collaborate.api.domain.Scope;
import collaborate.api.restclient.ICatalogClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;

@Service
public class DocumentService {

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private CipherService cipherService;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private RestTemplate restTemplate;

    public File downloadDocument(String documentId) throws Exception {
        Document document = catalogClient.getDocumentById(documentId);

        System.out.println("FOUND DOCUMENT: " + document.toString());
        Scope scope = new Scope();

        scope.setDatasourceId(document.getDatasourceId());
        scope.setScope(document.getScope());
        scope.setScopeId(document.getScopeId());
        scope.setOrganizationId(document.getOrganizationId());
        scope.setOrganizationName(document.getOrganizationName());

        AccessRequest accessRequest = scopeService.getAccessRequest(scope);

        String privateKeyAsString = apiProperties.getOrganizationPrivateKey();
        PrivateKey privateKey = CipherService.getPrivateKey(privateKeyAsString);

        String token = cipherService.decipher(accessRequest.getJwtToken(), privateKey);

        return restTemplate.execute(document.getDocumentUri(), HttpMethod.GET, clientHttpRequest -> clientHttpRequest.getHeaders().set(
                "auth-token",
                "Bearer " + token), clientHttpResponse -> {
            System.out.println(clientHttpResponse.getBody());
            File ret = File.createTempFile("download", "pdf");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
    }
}
