package com.example.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author hondayuki
 *
 */
public class LoginUser extends org.springframework.security.core.userdetails.User {
	
	private static final long serialVersionUID = 1L;
	/** ユーザー情報 */
	private final User user;
	
	/**
	 * 認可用のロールを設定
	 * 
	 * @param user
	 * @param authorityList
	 */
	public LoginUser(User user, Collection<GrantedAuthority> authorityList) {
		super(user.getEmail(), user.getPassword(), authorityList);
		this.user = user;
	}
	
	/**
	 * ユーザ情報を返す
	 * 
	 * @return
	 */
	public User getUser() {
		return user;
	}
}
