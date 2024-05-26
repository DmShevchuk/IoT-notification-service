package ru.iot.notification.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMqProperties {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private String port;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.iot.queue}")
    private String iotQueueName;

    @Value("${rabbitmq.iot.exchange}")
    private String iotExchangeName;

    @Value("${rabbitmq.iot.routing-key}")
    private String iotRoutingKey;

    @Value("${rabbitmq.parking.dlq-queue}")
    private String iotDlqName;

    @Value("${rabbitmq.parking.parking-queue}")
    private String iotParkingQueueName;

    @Value("${rabbitmq.parking.exchange}")
    private String iotParkingExchangeName;

    @Value("${rabbitmq.parking.routing-key}")
    private String iotParkingRoutingKey;

    @Value("${rabbitmq.ttl}")
    private int ttl;

    @Value("${rabbitmq.durable}")
    private boolean durable;

    @Value("${rabbitmq.exclusive}")
    private boolean exclusive;

    @Value("${rabbitmq.auto-delete}")
    private boolean autoDelete;

    @Value("${rabbitmq.parking.dlx-routing-key}")
    private String iotDlxRoutingKey;
}
