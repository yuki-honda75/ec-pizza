package com.example.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	@ModelAttribute
	public InsertUserForm setUpInsertUserForm() {
		return new InsertUserForm();
	}
	
	/**
	 * ログイン画面を表示
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public String toLogin(Model model, @RequestParam(required = false) String error) {
		if (error != null) {
			model.addAttribute("errorMessage", "正しい処理が行われなかったか、メールアドレスまたはパスワードが不正です");
		}
		return "login";
	}
	
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
	public String register(@Validated InsertUserForm form
						,BindingResult result) {
		//パスワードと確認パスワード照合
		if (!form.getPassword().equals(form.getConfirmPass())) {
			result.rejectValue("confirmPass", null, "パスワードと確認用パスワードが不一致です");
		}
		//メールアドレス確認
		if (userService.checkEmail(form.getEmail()) != null) {
			result.rejectValue("email", null, "そのメールアドレスはすでに使われています");
		}
		//一つでもエラーなら登録画面へ
		if (result.hasErrors()) {
			return toRegister();
		}
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
		return "redirect:/";
	}
}
