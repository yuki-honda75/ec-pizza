package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	public List<Item> findAll() {
		return itemRepository.findAll();
	}

	public Page<Item> showListPage(Pageable pageable) {
		return itemRepository.findAllPage(pageable);
	}

	public Page<Item> search(String name, Integer sortNum, Pageable pageable) {
		return itemRepository.findByCondition(name, sortNum, pageable);
	}

	public Item showDetail(Integer id) {
		return itemRepository.load(id);
	}

	public List<Topping> selectTopping(List<Integer> toppingIdList) {
		return itemRepository.findToppingById(toppingIdList);
	}
}
