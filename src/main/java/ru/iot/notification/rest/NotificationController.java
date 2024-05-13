package ru.iot.notification.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.iot.notification.dto.producer.NotificationEvent;
import ru.iot.notification.service.producer.KafkaProducer;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final KafkaProducer kafkaProducer;


    @PostMapping
    public void sendNotificationEvent(@RequestBody NotificationEvent notificationEvent) {
        kafkaProducer.sendNotificationEvent(notificationEvent);
    }





}
