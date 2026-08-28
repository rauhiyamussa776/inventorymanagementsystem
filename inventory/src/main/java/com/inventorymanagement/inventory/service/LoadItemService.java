package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.model.Load;
import com.inventorymanagement.inventory.model.LoadItem;
import com.inventorymanagement.inventory.model.LoadStatus;
import com.inventorymanagement.inventory.model.StockBatch;
import com.inventorymanagement.inventory.repository.ItemRepository;
import com.inventorymanagement.inventory.repository.LoadItemRepository;
import com.inventorymanagement.inventory.repository.LoadRepository;
import com.inventorymanagement.inventory.repository.StockBatchRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoadItemService {

    private final LoadItemRepository loadItemRepository;
    private final LoadRepository loadRepository;
    private final ItemRepository itemRepository;
    private final StockBatchRepository stockBatchRepository;

    public LoadItemService(
            LoadItemRepository loadItemRepository,
            LoadRepository loadRepository,
            ItemRepository itemRepository,
            StockBatchRepository stockBatchRepository) {

        this.loadItemRepository = loadItemRepository;
        this.loadRepository = loadRepository;
        this.itemRepository = itemRepository;
        this.stockBatchRepository = stockBatchRepository;
    }

    public LoadItem insertLoadItem(LoadItem loadItem) {

        // Check Load
        if (loadItem.getLoad() == null ||
                loadItem.getLoad().getId() == null) {

            throw new RuntimeException("Load is required");
        }

        // Check Item
        if (loadItem.getItem() == null ||
                loadItem.getItem().getId() == null) {

            throw new RuntimeException("Item is required");
        }

        Long loadId = loadItem.getLoad().getId();
        Long itemId = loadItem.getItem().getId();

        // Find Load
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() ->
                        new RuntimeException("Load not found"));

        // Find Item
        Items item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        // Load must still be PENDING
        if (load.getStatus() != LoadStatus.PENDING) {

            throw new RuntimeException(
                    "Cannot add items to a load that is not PENDING"
            );
        }

        // Validate quantity
        if (loadItem.getQuantity() == null ||
                loadItem.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        // Check duplicate item in the same Load
        List<LoadItem> existingItems =
                loadItemRepository.findByLoad(load);

        for (LoadItem existingItem : existingItems) {

            if (existingItem.getItem().getId().equals(item.getId())) {

                throw new RuntimeException(
                        "Item already exists in this load"
                );
            }
        }

        // Get available stock
        List<StockBatch> batches =
                stockBatchRepository
                        .findByItemAndQuantityRemainingGreaterThanOrderByReceivedDateAsc(
                                item,
                                BigDecimal.ZERO
                        );

        // Calculate total available stock
        BigDecimal availableStock = batches.stream()
                .map(StockBatch::getQuantityRemaining)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Check stock
        if (availableStock.compareTo(loadItem.getQuantity()) < 0) {

            throw new RuntimeException(
                    "Insufficient stock. Available stock: "
                            + availableStock
                            + ", requested quantity: "
                            + loadItem.getQuantity()
            );
        }

        loadItem.setLoad(load);
        loadItem.setItem(item);

        return loadItemRepository.save(loadItem);
    }

    public List<LoadItem> getAllLoadItems() {
        return loadItemRepository.findAll();
    }

    public LoadItem getLoadItemById(Long id) {

        return loadItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Load item not found"));
    }
}