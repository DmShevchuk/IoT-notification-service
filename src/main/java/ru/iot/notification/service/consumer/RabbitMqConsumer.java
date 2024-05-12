package ru.iot.notification.service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.iot.notification.service.producer.KafkaProducer;

@Slf4j
@Component
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final KafkaProducer kafkaProducer;

    private final ObjectMapper objectMapper;


    @RabbitListener(queues = "iot-queue")
    public void acceptNotification(String notificationEvent) throws JsonProcessingException {
        log.info("Получил {}", notificationEvent);
        kafkaProducer.sendNotificationEvent(objectMapper.readValue(notificationEvent, new TypeReference<>() {}));
    }
}
