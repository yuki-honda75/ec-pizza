package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * ユーザ登録
	 * 
	 * @param user
	 */
	public void register(User user) {
		String encodePass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePass);
		userRepository.insert(user);
	}
	
	/**
	 * メールアドレス重複確認
	 * 
	 * @param email
	 * @return
	 */
	public User checkEmail(String email) {
		return userRepository.findByMailAddress(email);
	}
}
