package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.model.InvoiceItem;
import com.inventorymanagement.inventory.model.InvoiceStatus;
import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.repository.InvoiceItemRepository;
import com.inventorymanagement.inventory.repository.InvoiceRepository;
import com.inventorymanagement.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public InvoiceItem insertInvoiceItem(InvoiceItem invoiceItem) {

        // Check invoice
        if (invoiceItem.getInvoice() == null ||
                invoiceItem.getInvoice().getId() == null) {

            throw new RuntimeException("Invoice is required");
        }

        // Check item
        if (invoiceItem.getItem() == null ||
                invoiceItem.getItem().getId() == null) {

            throw new RuntimeException("Item is required");
        }

        Long invoiceId = invoiceItem.getInvoice().getId();
        Long itemId = invoiceItem.getItem().getId();

        // Find invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found"));

        // Find item
        Items item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        // Validate invoice status
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException(
                    "Cannot add item to a paid invoice"
            );
        }

        // Validate quantity
        if (invoiceItem.getQuantity() == null ||
                invoiceItem.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        // Validate unit price
        if (invoiceItem.getUnitPrice() == null ||
                invoiceItem.getUnitPrice() <= 0) {

            throw new RuntimeException(
                    "Unit price must be greater than zero"
            );
        }

        invoiceItem.setInvoice(invoice);
        invoiceItem.setItem(item);

        // Calculate item total
        double totalPrice =
                invoiceItem.getQuantity()
                        * invoiceItem.getUnitPrice();

        invoiceItem.setTotalAmount(totalPrice);

        // Update invoice total
        double newInvoiceTotal =
                invoice.getTotalAmount() + totalPrice;

        invoice.setTotalAmount(newInvoiceTotal);

        // Save invoice
        invoiceRepository.save(invoice);

        // Save invoice item
        return invoiceItemRepository.save(invoiceItem);
    }

    public List<InvoiceItem> getAllInvoiceItems() {
        return invoiceItemRepository.findAll();
    }

    public InvoiceItem getInvoiceItemById(Long id) {
        return invoiceItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Invoice item not found"));
    }
}