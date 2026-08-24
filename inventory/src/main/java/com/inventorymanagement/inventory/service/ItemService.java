package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Items insertItem(Items item) {
        return itemRepository.save(item);
    }

    public List<Items> getAllItems() {
        return itemRepository.findAll();
    }

    public Items getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }
    public void deleteItem(Long id) {

    Items item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));

    itemRepository.delete(item);
}
}