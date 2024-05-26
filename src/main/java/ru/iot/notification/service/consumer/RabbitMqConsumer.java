package ru.iot.notification.service.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.iot.notification.config.RabbitMqProperties;
import ru.iot.notification.service.producer.KafkaProducer;

@Slf4j
@Component
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properties;

    private static final String IOT_QUEUE = "iot-queue";

    @RabbitListener(queues = IOT_QUEUE)
    public void acceptNotification(Message message) {
        var messageAsString = new String(message.getBody());
        try {
            log.info("Получение сообщения о необходимости заказа еды {}", messageAsString);
            kafkaProducer.sendNotificationEvent(objectMapper.readValue(message.getBody(), new TypeReference<>() {}));
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: {}", messageAsString);
            retryOrParkMessage(message);
        }
    }

    private void retryOrParkMessage(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        int retryCount = (int) messageProperties.getHeaders().getOrDefault("x-retry-count", 0);
        if (retryCount < 5) {
            messageProperties.setHeader("x-retry-count", retryCount + 1);
            rabbitTemplate.send(properties.getIotExchangeName(), properties.getIotRoutingKey(), message);
        } else {
            rabbitTemplate.send(properties.getIotParkingExchangeName(), properties.getIotParkingRoutingKey(), message);
        }
    }



}

