package ru.iot.notification.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

    private final RabbitMqProperties properties;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(properties.getHost());
        cachingConnectionFactory.setUsername(properties.getUsername());
        cachingConnectionFactory.setPassword(properties.getPassword());
        return cachingConnectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(converter());
        return template;
    }

    @Bean
    @Qualifier("mainQueue")
    public Queue myQueue() {
        deleteQueueIfExists(properties.getIotQueueName());

        Map<String, Object> args = new HashMap<>();

        args.put("x-dead-letter-exchange", properties.getIotParkingExchangeName());
        args.put("x-dead-letter-routing-key", properties.getIotDlxRoutingKey());
        args.put("x-message-ttl", properties.getTtl());

        return new Queue(
                properties.getIotQueueName(),
                properties.isDurable(),
                properties.isExclusive(),
                properties.isAutoDelete(), args
        );
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(properties.getIotExchangeName(), true, false);
    }

    @Bean
    public Binding binding(@Qualifier("mainQueue") Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(properties.getIotRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Qualifier("dlq")
    public Queue dlq() {
        return new Queue(properties.getIotDlqName());
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(properties.getIotParkingExchangeName());
    }

    @Bean
    public Binding dlqBinding(@Qualifier("dlq") Queue dlq) {
        return BindingBuilder.bind(dlq).to(dlxExchange()).with("dlx-routing-key");
    }

    @Bean
    @Qualifier("parkingQueue")
    public Queue parkingQueue() {
        return new Queue(properties.getIotParkingQueueName());
    }

    @Bean
    public Binding parkingBinding(@Qualifier("parkingQueue") Queue parkingQueue) {
        return BindingBuilder.bind(parkingQueue).to(dlxExchange()).with(properties.getIotParkingRoutingKey());
    }
    
    private void deleteQueueIfExists(String queueName) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        try {
            rabbitAdmin.deleteQueue(queueName);
        } catch (Exception e) {
            System.err.println("Failed to delete queue: " + queueName);
        }
    }
}
