package com.inventorymanagement.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.*;

import lombok.Data;
@Entity
@Data
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long id;
    @Column(nullable=false)
    private String  itemsNames;
}
