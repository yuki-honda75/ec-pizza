package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.domain.Item;
import com.example.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author hondayuki
 *
 */
@RestController
@RequestMapping("/item")
public class ItemRestController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET)
    public Map<String,String[]> itemAutocomplete() {
        List<Item> itemList = itemService.showList();
        String[] items = new String[itemList.size()];
        int i = 0;
        for (Item item : itemList) {
            items[i] = item.getName();
            i++;
        }

        Map<String, String[]> map = new HashMap<>();
        map.put("items", items);

        return map;
    }
}
