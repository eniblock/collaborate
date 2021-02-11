package collaborate.catalog.service;

import collaborate.catalog.domain.Document;
import collaborate.catalog.repository.DocumentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @RabbitListener(queues = "#{documentCreateQueue.name}")
    public void create(Document document) {
        document = documentRepository.save(document);

        System.out.println(document);
    }
}
