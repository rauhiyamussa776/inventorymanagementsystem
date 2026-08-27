package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.Load;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadRepository extends JpaRepository<Load, Long> {
}