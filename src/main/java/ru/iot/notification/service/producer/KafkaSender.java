package ru.iot.notification.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.iot.notification.dto.producer.NotificationEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSender {

    @Value("${kafka-topics.send-notification}")
    private String notificationTopic;

    private final KafkaTemplate<String , Object> kafkaTemplate;

    public void sendNotification(NotificationEvent notificationEvent) {
        kafkaTemplate.send(notificationTopic, notificationEvent.getClientId().toString(), notificationEvent);
        log.info("Отправка сообщения о необходимости заказа еды {}", notificationEvent);
    }

}