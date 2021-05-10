package collaborate.catalog.service;

import collaborate.catalog.domain.Document;
import collaborate.catalog.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @RabbitListener(queues = "#{documentCreateQueue.name}")
    public void create(Document document) {
        document = documentRepository.save(document);
        LOG.info("Saving document : " + document);
    }
}
