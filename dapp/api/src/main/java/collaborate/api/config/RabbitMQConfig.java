package collaborate.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    Queue datasourceSynchronizeQueue() {
        return new Queue("datasource.synchronize", true);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("datasource");
    }

    @Bean
    public Binding DatasourceSynchronizeBinding(TopicExchange topic,
                             Queue queue) {
        return BindingBuilder.bind(queue)
                .to(topic)
                .with("datasource.synchronize");
    }

    @Bean
    public Binding DatasourceCreaedBinding(TopicExchange topic,
                             Queue queue) {
        return BindingBuilder.bind(queue)
                .to(topic)
                .with("datasource.created");
    }
}
