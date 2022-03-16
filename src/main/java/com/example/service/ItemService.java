package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.Item;
import com.example.domain.Topping;
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

	public Item showDetail(Integer id) {
		return itemRepository.load(id);
	}

	public List<Topping> selectTopping(List<Integer> toppingIdList) {
		return itemRepository.findToppingById(toppingIdList);
	}
}
