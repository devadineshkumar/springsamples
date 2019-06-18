package spring.user.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.user.app.model.UserModel;

public interface UserModelDao extends JpaRepository<UserModel, Long> {

	UserModel getUserById(Long id);
	
	

}
