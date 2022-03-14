package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author hondayuki
 *
 */
@Controller
@RequestMapping("")
public class UserController {
	
	@RequestMapping("/toRegister")
	public String toRegister() {
		return "register_user";
	}
}
