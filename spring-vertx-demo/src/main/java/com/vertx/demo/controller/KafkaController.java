package com.vertx.demo.controller;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    @Autowired
    private Vertx vertx;

    @PostMapping(value = "/send", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        vertx.eventBus().publish("kafka.outgoing", message);
        return ResponseEntity.ok("Message sent to Kafka");
    }

//    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> streamMessages() {
//        return Flux.interval(Duration.ofSeconds(1))
//                .map(i -> "Message #" + i);
//    }



}