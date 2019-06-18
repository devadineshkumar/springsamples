package spring.rest.app;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {
	
	@RequestMapping(value = "/")
	public String healthCheck() {
		return "Server started successfully";
	}

}
