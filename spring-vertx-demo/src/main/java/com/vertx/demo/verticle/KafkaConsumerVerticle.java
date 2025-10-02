package com.vertx.demo.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.Map;


public class KafkaConsumerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "172.18.247.175:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "vertx-group");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "true");

        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);

        consumer.subscribe("my-topic");

        consumer.handler(record -> {
            System.out.println("Received from Kafka: " + record.value());
            vertx.eventBus().publish("kafka.incoming", record.value());
        });
    }
}