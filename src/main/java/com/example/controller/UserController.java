package com.example.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.User;
import com.example.form.InsertUserForm;
import com.example.service.UserService;

/**
 * 
 * @author hondayuki
 *
 */
@Controller
@RequestMapping("")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 新規登録画面を表示
	 * 
	 * @return
	 */
	@RequestMapping("/toRegister")
	public String toRegister() {
		return "register_user";
	}
	
	/**
	 * 新規登録処理をする
	 * 
	 * @return
	 */
	@RequestMapping("/register")
	public String register(InsertUserForm form) {
		//確認用
		User user = new User();
//		user.setName("sample");
//		user.setEmail("sample@sample.com");
//		user.setAddress("千葉県");
//		user.setZipcode("222-2222");
//		user.setTelephone("01-2345-6789");
//		user.setPassword("12345678");
		BeanUtils.copyProperties(form, user);
		userService.register(user);
		return "item_list_pizza";
	}
}
