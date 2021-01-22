package collaborate.catalog.controller;

import collaborate.catalog.domain.Data;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("data")
public class DataController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TopicExchange topic;

    @PostMapping()
    public ResponseEntity<Data> add(@RequestBody Data data) {
        rabbitTemplate.convertAndSend(
                topic.getName(),
                "data.create",
                data
        );

        return ResponseEntity.ok(data);
    }
}
