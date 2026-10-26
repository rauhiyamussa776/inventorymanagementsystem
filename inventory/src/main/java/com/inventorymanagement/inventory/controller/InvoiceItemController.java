package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.model.InvoiceItem;
import com.inventorymanagement.inventory.service.InvoiceItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice-items")
public class InvoiceItemController {

    private final InvoiceItemService invoiceItemService;

    public InvoiceItemController(InvoiceItemService invoiceItemService) {
        this.invoiceItemService = invoiceItemService;
    }

    @PostMapping
    public InvoiceItem insertInvoiceItem(@RequestBody InvoiceItem invoiceItem) {
        return invoiceItemService.insertInvoiceItem(invoiceItem);
    }

    @GetMapping
    public List<InvoiceItem> getAllInvoiceItems() {
        return invoiceItemService.getAllInvoiceItems();
    }

    @GetMapping("/{id}")
    public InvoiceItem getInvoiceItemById(@PathVariable Long id) {
        return invoiceItemService.getInvoiceItemById(id);
    }
}