package com.inventorymanagement.inventory.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "load_items")
@Data
public class LoadItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;
}