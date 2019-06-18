package spring.user.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import spring.user.app.PropsConfiguration;
import spring.user.app.model.UserModel;
import spring.user.app.service.UserService;

@RestController
@RequestMapping("users")
public class UserModelRestController {
	
	@Autowired
	private PropsConfiguration propsConfiguration;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(method = RequestMethod.GET, path="/{id}", consumes ="application/json")
	public UserModel getUserById(@PathVariable Long id) {
		
		System.out.println("Property loaded in : username : "+propsConfiguration.getInnerProp().getUsername());
		System.out.println("Property loaded in : username : "+propsConfiguration.getInnerProp().getRolename());
		System.out.println("Property loaded in : username : "+propsConfiguration.getStringValue());
		System.out.println("Property loaded in : username : "+propsConfiguration.getRandomString());
		System.out.println("Property loaded in : username : "+propsConfiguration.getIntegerValue());
		System.out.println("Property loaded in : username : "+propsConfiguration.getRandomValue());
		
		return userService.getUserById(id);
	}

}
