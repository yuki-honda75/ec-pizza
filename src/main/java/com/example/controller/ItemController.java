package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Item;
import com.example.service.ItemService;

/**
 * 
 * @author hondayuki
 *
 */
@Controller
@RequestMapping("/item")
public class ItemController {
	@Autowired
	private ItemService itemService;
	
	//仮マッピング
	@RequestMapping("/showList")
	public String showList(Model model) {
		List<Item> itemList = itemService.showList();
		model.addAttribute("itemList", itemList);
		
		return "item_list_pizza";
	}
}
