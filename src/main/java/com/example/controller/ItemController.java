package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author hondayuki
 *
 */
@Controller
@RequestMapping("/item")
public class ItemController {
	//仮マッピング
	@RequestMapping("/showList")
	public String showList() {
		return "item_list_pizza";
	}
}
