package spring.user.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootWebclientApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(SpringBootWebclientApplication.class, args);

		WebClientService cacheObject = ctx.getBean(WebClientService.class);
		// calling getBook method first time.
		Mono<String> userModelResponse = cacheObject.someRestCall();
		System.out.println(userModelResponse.block());

	}

}
