package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Item;
import com.example.form.InsertOrderForm;
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

	@ModelAttribute
	public InsertOrderForm setUpInsertOrderForm() {
		return new InsertOrderForm();
	}
	
	/**
	 * 商品一覧を表示する,検索する
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/showList")
	public String showList(String name, Integer sortNum, Model model, @PageableDefault(size = 10, page = 0) Pageable pageable) {
		List<Item> itemList = null;
		Page<Item> page = null;
		if (name != null || sortNum != null) {
			itemList = itemService.search(name, sortNum);
		} else {
			page = itemService.showListPage(pageable);
		}
		model.addAttribute("page", page);
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
	public String showDetail(InsertOrderForm form, Integer id, Model model) {
		Item item = itemService.showDetail(id);
		model.addAttribute("item", item);
		if (form.getSize() == null) {
			form.setSize("0");
		}

		return "item_detail";
	}
}
