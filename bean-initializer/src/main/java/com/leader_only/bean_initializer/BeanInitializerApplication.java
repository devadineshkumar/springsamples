package com.leader_only.bean_initializer;

import com.leader_only.bean_initializer.config.LeaderOnlyBeanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeanInitializerApplication {

	@Autowired
	private LeaderOnlyBeanConfig leaderOnlyBeanConfig;

	public static void main(String[] args) {
		SpringApplication.run(BeanInitializerApplication.class, args);
	}

}
