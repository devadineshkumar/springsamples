package spring.user.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties()
public class SpringUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringUsersApplication.class, args);
		System.out.println();
	}

}
