package com.inventorymanagement.inventory.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;
    @Column(nullable = false)
    private LocalDate dueDate;


    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private Double totalAmount = 0.0;

    @Column(nullable = false)
    private Double paidAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;
}

