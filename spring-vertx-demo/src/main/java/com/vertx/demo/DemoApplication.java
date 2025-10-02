package com.vertx.demo;

import com.vertx.demo.verticle.KafkaConsumerVerticle;
import com.vertx.demo.verticle.KafkaProducerVerticle;
import com.vertx.demo.verticle.MyVerticle;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private Vertx vertx;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		vertx.deployVerticle(new KafkaConsumerVerticle());
		vertx.deployVerticle(new KafkaProducerVerticle());
		vertx.deployVerticle(new MyVerticle());
	}

}
