package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.model.InvoiceItem;
import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.repository.InvoiceItemRepository;
import com.inventorymanagement.inventory.repository.InvoiceRepository;
import com.inventorymanagement.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;

    public InvoiceItemService(
            InvoiceItemRepository invoiceItemRepository,
            InvoiceRepository invoiceRepository,
            ItemRepository itemRepository) {

        this.invoiceItemRepository = invoiceItemRepository;
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
    }

    public InvoiceItem insertInvoiceItem(InvoiceItem invoiceItem) {

        Long invoiceId = invoiceItem.getInvoice().getId();
        Long itemId = invoiceItem.getItem().getId();

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Items item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        invoiceItem.setInvoice(invoice);
        invoiceItem.setItem(item);

        double totalPrice =
                invoiceItem.getQuantity() * invoiceItem.getUnitPrice();

        invoiceItem.setTotalAmount(totalPrice);

        return invoiceItemRepository.save(invoiceItem);
    }

    public List<InvoiceItem> getAllInvoiceItems() {
        return invoiceItemRepository.findAll();
    }

    public InvoiceItem getInvoiceItemById(Long id) {
        return invoiceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice item not found"));
    }
}