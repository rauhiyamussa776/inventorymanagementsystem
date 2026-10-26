package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.Load;
import com.inventorymanagement.inventory.model.LoadItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LoadItemRepository extends JpaRepository<LoadItem, Long> {
    List<LoadItem> findByLoad(Load load);
}