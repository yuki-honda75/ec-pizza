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
	
	/**
	 * 商品一覧を表示する,検索する
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/showList")
	public String showList(String name, Integer sortNum, Model model) {
		List<Item> itemList = null;
		if (name != null || sortNum != null) {
			itemList = itemService.search(name, sortNum);
		} else {
			itemList = itemService.showList();
		}
		model.addAttribute("name", name);
		model.addAttribute("sortNum", sortNum);
		model.addAttribute("itemList", itemList);
		
		return "item_list_pizza";
	}

	/**
	 * 商品詳細画面を表示する
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/detail")
	public String showDetail(Integer id, Model model) {
		Item item = itemService.showDetail(id);
		model.addAttribute("item", item);

		return "item_detail";
	}
}
