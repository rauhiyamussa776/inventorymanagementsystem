package com.inventorymanagement.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

import lombok.Data;


@Entity
@Table(name = "stock_movements")
@Data
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

   @Column(nullable = false, precision = 12, scale = 2)
private BigDecimal quantity;

    @Column(nullable = false)
    private LocalDateTime movementDate;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}

