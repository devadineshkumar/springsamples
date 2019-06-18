package spring.user.app.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.user.app.dao.UserModelDao;
import spring.user.app.model.UserModel;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserModelDao userModelDao;

	@Override
	public UserModel getUserById(Long id) {
		return userModelDao.getUserById(id);
	}
	
	

}
