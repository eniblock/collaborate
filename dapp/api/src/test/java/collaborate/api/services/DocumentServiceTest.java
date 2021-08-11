package collaborate.api.services;

import static org.junit.Assert.assertNotNull;

import collaborate.api.domain.DownloadDocument;
import java.io.File;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;


@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder tmpFolder2 = new TemporaryFolder();

    @Rule
    public TemporaryFolder tmpFolder3 = new TemporaryFolder();

    @InjectMocks
    DocumentService documentService;

    private static final String[] DOCUMENTS_IDS = {"606ec869ae355f466d422d49","606ec869ae355f466d422d4a", "606ed869ae355f466d422d4b"};

    @Before
    public void setup(){
        documentService = Mockito.spy(new DocumentService());
    }


    @Test
    public void itShouldBuildZipWithoutExceptionWhen2FilesHaveSameName() throws Exception {
        HttpServletResponse response = new MockHttpServletResponse();
        File testFile = tmpFolder.newFile("myFile");
        File testFile2 = tmpFolder2.newFile("myFile");
        File testFile3 = tmpFolder3.newFile("myFile");

        DownloadDocument downloadDocument = new DownloadDocument("myFile", testFile);
        DownloadDocument downloadDocument2 = new DownloadDocument("myFile", testFile2);
        DownloadDocument downloadDocument3 = new DownloadDocument("myFile", testFile3);
        Mockito.doReturn(downloadDocument).when(documentService).downloadDocument("606ec869ae355f466d422d49");
        Mockito.doReturn(downloadDocument2).when(documentService).downloadDocument("606ec869ae355f466d422d4a");
        Mockito.doReturn(downloadDocument3).when(documentService).downloadDocument("606ed869ae355f466d422d4b");
        ZipOutputStream zipOutputStream = documentService.buildDocumentsZip(DOCUMENTS_IDS, response.getOutputStream());
        assertNotNull(zipOutputStream);
    }


}
