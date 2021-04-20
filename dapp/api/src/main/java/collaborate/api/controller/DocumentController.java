package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.AccessRequest;
import collaborate.api.domain.Document;
import collaborate.api.domain.DownloadDocument;
import collaborate.api.domain.Scope;
import collaborate.api.restclient.ICatalogClient;
import collaborate.api.services.DocumentService;
import collaborate.api.services.ScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.UUID;

@RestController
public class DocumentController {

    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private ICatalogClient catalogClient;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private DocumentService documentService;

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}/documents")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<Page<Document>> listByScope(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId, Pageable pageable, @RequestParam(required = false, defaultValue = "") String q) {
        Page<Document> documents = catalogClient.getDocumentsByScope(organizationId, datasourceId, scopeId, pageable, q);

        return ResponseEntity.ok(documents);
    }

    @GetMapping("documents")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<Page<Document>> list(Pageable pageable, @RequestParam(required = false, defaultValue = "") String q) {
        Page<Document> documents = catalogClient.getDocuments(pageable, q);

        for (Document document : documents) {
            Scope scope = Scope.createFromDocument(document);
            AccessRequest accessRequest = scopeService.getAccessRequest(scope);

            document.setStatusFromAccessRequest(accessRequest);
        }

        return ResponseEntity.ok(documents);
    }

    @GetMapping("documents/{id}/downloads")
    public void download(@PathVariable("id") String id, HttpServletResponse response) throws Exception {
        DownloadDocument downloadDocument = documentService.downloadDocument(id);

        FileInputStream in = new FileInputStream(downloadDocument.getFile());

        response.setHeader("Content-Disposition", "attachment; filename="+ downloadDocument.getFileName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentLengthLong(downloadDocument.getFile().length());

        OutputStream out = response.getOutputStream();

        // copy from in to out
        IOUtils.copy(in,out);

        out.close();
        in.close();
        downloadDocument.getFile().delete();
    }

    @PostMapping(value = "/downloads", produces="application/zip")
    public void downloadList(@RequestBody String[] documentIds, HttpServletResponse response) throws Exception {

        response.setHeader("Content-Disposition", "attachment; filename=download.zip");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        documentService.buildDocumentsZip(documentIds, response.getOutputStream());
    }

}
