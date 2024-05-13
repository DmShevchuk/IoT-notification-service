package ru.iot.notification.service.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.iot.notification.dto.producer.NotificationEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaSender kafkaSender;


    public void sendNotificationEvent(NotificationEvent notificationEvent) {
        kafkaSender.sendNotification(notificationEvent);
    }

}
