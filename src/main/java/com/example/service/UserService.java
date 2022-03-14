package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.User;
import com.example.repository.UserRepository;

/**
 * 
 * @author hondayuki
 *
 */
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * ユーザ登録
	 * 
	 * @param user
	 */
	public void register(User user) {
		userRepository.insert(user);
	}
}
