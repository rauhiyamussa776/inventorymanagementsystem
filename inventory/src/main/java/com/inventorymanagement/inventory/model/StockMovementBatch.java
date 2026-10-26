package com.inventorymanagement.inventory.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_movement_batches")
@Data
public class StockMovementBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_movement_id", nullable = false)
    private StockMovement stockMovement;

    @ManyToOne
    @JoinColumn(name = "stock_batch_id", nullable = false)
    private StockBatch stockBatch;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;
}