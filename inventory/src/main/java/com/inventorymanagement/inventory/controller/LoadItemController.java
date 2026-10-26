package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.model.LoadItem;
import com.inventorymanagement.inventory.service.LoadItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/load-items")
public class LoadItemController {

    private final LoadItemService loadItemService;

    public LoadItemController(LoadItemService loadItemService) {
        this.loadItemService = loadItemService;
    }

    @PostMapping
    public LoadItem insertLoadItem(@RequestBody LoadItem loadItem) {
        return loadItemService.insertLoadItem(loadItem);
    }

    @GetMapping
    public List<LoadItem> getAllLoadItems() {
        return loadItemService.getAllLoadItems();
    }

    @GetMapping("/{id}")
    public LoadItem getLoadItemById(@PathVariable Long id) {
        return loadItemService.getLoadItemById(id);
    }
}