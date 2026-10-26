package com.inventorymanagement.inventory.model;

import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Data

public class StockBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;

    @Column(nullable = false, unique = true)
    private String batchNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityReceived;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityRemaining;

    @Column(nullable = false)
    private LocalDate receivedDate;

  
    private LocalDate expiryDate;
}
