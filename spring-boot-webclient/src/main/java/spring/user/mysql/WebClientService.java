package spring.user.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class WebClientService {

	private final WebClient webClient;

	@Autowired
	public WebClientService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080/").build();
	}

	public Mono<String> someRestCall() {
		System.out.println("WebClient : hashcode check : " + webClient.hashCode());
		return webClient.get().uri("users/1?id=2").accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);
	}

}
