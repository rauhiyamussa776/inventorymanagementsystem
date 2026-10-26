package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.model.MovementType;
import com.inventorymanagement.inventory.model.StockBatch;
import com.inventorymanagement.inventory.model.StockMovement;
import com.inventorymanagement.inventory.model.StockMovementBatch;
import com.inventorymanagement.inventory.repository.ItemRepository;
import com.inventorymanagement.inventory.repository.StockBatchRepository;
import com.inventorymanagement.inventory.repository.StockMovementBatchRepository;
import com.inventorymanagement.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final StockBatchRepository stockBatchRepository;
    private final StockMovementBatchRepository stockMovementBatchRepository;

    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ItemRepository itemRepository,
            StockBatchRepository stockBatchRepository,
            StockMovementBatchRepository stockMovementBatchRepository) {

        this.stockMovementRepository = stockMovementRepository;
        this.itemRepository = itemRepository;
        this.stockBatchRepository = stockBatchRepository;
        this.stockMovementBatchRepository = stockMovementBatchRepository;
    }

    @Transactional
    public StockMovement insertStockMovement(StockMovement stockMovement) {

        // Check item
        if (stockMovement.getItem() == null ||
                stockMovement.getItem().getId() == null) {

            throw new RuntimeException("Item is required");
        }

        // Check movement type
        if (stockMovement.getType() == null) {

            throw new RuntimeException("Movement type is required");
        }

        // Get item ID
        Long itemId = stockMovement.getItem().getId();

        // Check if item exists
        Items item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        stockMovement.setItem(item);

        // Set movement date automatically
        if (stockMovement.getMovementDate() == null) {
            stockMovement.setMovementDate(LocalDateTime.now());
        }

        // Validate quantity
        if (stockMovement.getQuantity() == null ||
                stockMovement.getQuantity()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        // Save movement first
        StockMovement savedMovement =
                stockMovementRepository.save(stockMovement);

        // Only perform FIFO when stock is going OUT
        if (stockMovement.getType() == MovementType.OUT) {

            BigDecimal quantityToRemove =
                    stockMovement.getQuantity();

            // Get available batches from oldest to newest
            List<StockBatch> batches =
                    stockBatchRepository
                            .findByItemAndQuantityRemainingGreaterThanOrderByReceivedDateAsc(
                                    item,
                                    BigDecimal.ZERO
                            );

            // Calculate total available stock
            BigDecimal totalAvailable = batches.stream()
                    .map(StockBatch::getQuantityRemaining)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Check if enough stock exists
            if (totalAvailable.compareTo(quantityToRemove) < 0) {

                throw new RuntimeException(
                        "Insufficient stock. Available stock: "
                                + totalAvailable
                                + ", requested quantity: "
                                + quantityToRemove
                );
            }

            BigDecimal remainingToRemove = quantityToRemove;

            // FIFO
            for (StockBatch batch : batches) {

                if (remainingToRemove
                        .compareTo(BigDecimal.ZERO) <= 0) {

                    break;
                }

                BigDecimal batchRemaining =
                        batch.getQuantityRemaining();

                BigDecimal quantityTaken;

                // Use the whole batch
                if (batchRemaining
                        .compareTo(remainingToRemove) <= 0) {

                    quantityTaken = batchRemaining;

                    batch.setQuantityRemaining(
                            BigDecimal.ZERO
                    );

                    remainingToRemove =
                            remainingToRemove.subtract(
                                    quantityTaken
                            );
                }

                // Use only part of the batch
                else {

                    quantityTaken = remainingToRemove;

                    batch.setQuantityRemaining(
                            batchRemaining.subtract(
                                    quantityTaken
                            )
                    );

                    remainingToRemove =
                            BigDecimal.ZERO;
                }

                // Save updated batch
                stockBatchRepository.save(batch);

                // Record which batch was used
                StockMovementBatch movementBatch =
                        new StockMovementBatch();

                movementBatch.setStockMovement(savedMovement);
                movementBatch.setStockBatch(batch);
                movementBatch.setQuantity(quantityTaken);

                stockMovementBatchRepository.save(
                        movementBatch
                );
            }
        }

        return savedMovement;
    }

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public StockMovement getStockMovementById(Long id) {

        return stockMovementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock movement not found"
                        ));
    }
}