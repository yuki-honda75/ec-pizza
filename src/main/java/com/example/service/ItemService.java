package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.Item;
import com.example.repository.ItemRepository;

/**
 * 
 * @author hondayuki
 *
 */
@Service
public class ItemService {
	@Autowired
	private ItemRepository itemRepository;
	
	public List<Item> showList() {
		return itemRepository.findAll();
	}

	public List<Item> search(String name, Integer sortNum) {
		return itemRepository.findByCondition(name, sortNum);
	}
}