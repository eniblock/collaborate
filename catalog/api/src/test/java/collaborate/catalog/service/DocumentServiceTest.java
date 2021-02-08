package collaborate.catalog.service;

import collaborate.catalog.domain.Document;
import collaborate.catalog.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @InjectMocks
    DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Test
    void create() {
        Document document = new Document();

        documentService.create(document);

        verify(documentRepository, times(1)).save(document);
    }
}