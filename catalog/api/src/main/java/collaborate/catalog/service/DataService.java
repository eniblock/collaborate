package collaborate.catalog.service;

import collaborate.catalog.domain.Data;
import collaborate.catalog.repository.DataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    private DataRepository dataRepository;

    @RabbitListener(queues = "#{dataCreateQueue.name}")
    public void create(Data data) {
        data = dataRepository.save(data);

        System.out.println(data);
    }
}
