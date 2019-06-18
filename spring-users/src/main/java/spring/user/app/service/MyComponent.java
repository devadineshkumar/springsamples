package spring.user.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class MyComponent {

	
	@Autowired
	public MyComponent(ApplicationArguments args) {
		System.out.println("My components args imports from autowire");
	}
}
