package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}