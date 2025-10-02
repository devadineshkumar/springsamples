package com.vertx.demo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.impl.KafkaProducerRecordImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerVerticle extends AbstractVerticle {

    private KafkaProducer<String, String> producer;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Override
    public void start() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "172.18.247.175:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");

        producer = KafkaProducer.create(vertx, config);

        vertx.eventBus().consumer("kafka.outgoing", message -> {
            String value = message.body().toString();
            producer.write(new KafkaProducerRecordImpl<>("my-topic", "key", value));
        });

        vertx.eventBus().consumer("kafka.incoming", message -> {
            String value = message.body().toString();
            System.out.println(" Kafka incomign message from event bus of vert.x " + value);
        });
    }
}