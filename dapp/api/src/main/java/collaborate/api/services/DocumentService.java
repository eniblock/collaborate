package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.Document;
import collaborate.api.domain.DownloadDocument;
import collaborate.api.domain.Scope;
import collaborate.api.restclient.ICatalogClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

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

    public DownloadDocument downloadDocument(String documentId) throws Exception {
        Document document = catalogClient.getDocumentById(documentId);

        if (document == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Scope scope = Scope.createFromDocument(document);

        System.out.println("SCOPE: " + scope.toString());

        AccessRequest accessRequest = scopeService.getAccessRequest(scope);

        if (accessRequest == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String privateKeyAsString = apiProperties.getOrganizationPrivateKey();
        PrivateKey privateKey = CipherService.getPrivateKey(privateKeyAsString);

        String token = cipherService.decipher(accessRequest.getJwtToken(), privateKey);

        return restTemplate.execute(document.getDocumentUri(), HttpMethod.GET, clientHttpRequest -> clientHttpRequest.getHeaders().set(
                "Authorization",
                "Bearer " + token), clientHttpResponse -> {
            String contentDisposition = clientHttpResponse.getHeaders().get("Content-Disposition").get(0);
            String filename = contentDisposition.substring(contentDisposition.indexOf("\"") + 1, contentDisposition.lastIndexOf("\""));

            File ret = File.createTempFile(filename, "");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));

            return new DownloadDocument(filename, ret);
        });
    }

    /**
     * Build a Zip containing the documents list to download.
     *
     * @param documentIds
     * @param outPutStream
     * @throws Exception
     */
    public ZipOutputStream buildDocumentsZip(String[] documentIds, ServletOutputStream outPutStream) throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outPutStream);
        Set<String> fileNameSet = new LinkedHashSet<>();
        for (String documentId : documentIds) {
            DownloadDocument downloadDocument = downloadDocument(documentId);
            String fileName = downloadDocument.getFileName();
            for (int i = 1; !fileNameSet.add(fileName); i++) {
                // If the value is already present in the set, an index is added
                fileName = downloadDocument.getFileName() + '_' + i;
                LOG.info("Identical file name found : " + fileName);
            }
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            FileInputStream fileInputStream = new FileInputStream(downloadDocument.getFile());

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();

            downloadDocument.getFile().delete();
        }

        zipOutputStream.close();
        return zipOutputStream;
    }
}
