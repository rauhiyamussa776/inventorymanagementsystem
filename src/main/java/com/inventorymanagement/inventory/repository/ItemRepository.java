package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.Items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Items, Long> {
}