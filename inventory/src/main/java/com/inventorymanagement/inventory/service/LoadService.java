package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Company;
import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.model.InvoiceItem;
import com.inventorymanagement.inventory.model.Load;
import com.inventorymanagement.inventory.model.LoadItem;
import com.inventorymanagement.inventory.model.LoadStatus;
import com.inventorymanagement.inventory.model.MovementType;
import com.inventorymanagement.inventory.model.StockMovement;
import com.inventorymanagement.inventory.repository.CampanyRepository;
import com.inventorymanagement.inventory.repository.InvoiceItemRepository;
import com.inventorymanagement.inventory.repository.InvoiceRepository;
import com.inventorymanagement.inventory.repository.LoadItemRepository;
import com.inventorymanagement.inventory.repository.LoadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoadService {

    private final LoadRepository loadRepository;
    private final CampanyRepository companyRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final LoadItemRepository loadItemRepository;
    private final StockMovementService stockMovementService;

    public LoadService(
            LoadRepository loadRepository,
            CampanyRepository companyRepository,
            InvoiceRepository invoiceRepository,
            InvoiceItemRepository invoiceItemRepository,
            LoadItemRepository loadItemRepository,
            StockMovementService stockMovementService) {

        this.loadRepository = loadRepository;
        this.companyRepository = companyRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.loadItemRepository = loadItemRepository;
        this.stockMovementService = stockMovementService;
    }

    public Load insertLoad(Load load) {

        // Check company
        if (load.getCompany() == null ||
                load.getCompany().getId() == null) {

            throw new RuntimeException("Company is required");
        }

        Long companyId = load.getCompany().getId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        load.setCompany(company);

        // Invoice is optional while creating the load
        if (load.getInvoice() != null &&
                load.getInvoice().getId() != null) {

            Long invoiceId = load.getInvoice().getId();

            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() ->
                            new RuntimeException("Invoice not found"));

            // Invoice must belong to the same company
            if (!invoice.getCompany().getId().equals(company.getId())) {

                throw new RuntimeException(
                        "Invoice does not belong to this company"
                );
            }

            load.setInvoice(invoice);
        }

        // Set created date automatically
        if (load.getCreatedDate() == null) {
            load.setCreatedDate(LocalDateTime.now());
        }

        // New load starts as PENDING
        load.setStatus(LoadStatus.PENDING);

        return loadRepository.save(load);
    }

    public List<Load> getAllLoads() {
        return loadRepository.findAll();
    }

    public Load getLoadById(Long id) {

        return loadRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Load not found"));
    }

    /*
     * Validate LoadItems against InvoiceItems
     */
    private void validateLoadItemsWithInvoice(Load load) {

        // Load must have invoice
        if (load.getInvoice() == null ||
                load.getInvoice().getId() == null) {

            throw new RuntimeException(
                    "Load must have an invoice before it can be marked as READY"
            );
        }

        Invoice invoice = load.getInvoice();

        // Get load items
        List<LoadItem> loadItems =
                loadItemRepository.findByLoad(load);

        if (loadItems.isEmpty()) {

            throw new RuntimeException(
                    "Cannot mark load as READY without items"
            );
        }

        // Get invoice items
        List<InvoiceItem> invoiceItems =
                invoiceItemRepository.findByInvoice(invoice);

        if (invoiceItems.isEmpty()) {

            throw new RuntimeException(
                    "Invoice has no items"
            );
        }

        // Check every LoadItem
        for (LoadItem loadItem : loadItems) {

            boolean found = false;

            for (InvoiceItem invoiceItem : invoiceItems) {

                // Compare item IDs
                if (invoiceItem.getItem().getId()
                        .equals(loadItem.getItem().getId())) {

                    found = true;

                    BigDecimal loadQuantity =
                            loadItem.getQuantity();

                    BigDecimal invoiceQuantity =
                            BigDecimal.valueOf(
                                    invoiceItem.getQuantity()
                            );

                    // Check quantity
                    if (loadQuantity.compareTo(invoiceQuantity) != 0) {

                        throw new RuntimeException(
                                "Quantity mismatch for item: "
                                        + loadItem.getItem().getItemsNames()
                                        + ". Load quantity: "
                                        + loadQuantity
                                        + ", Invoice quantity: "
                                        + invoiceQuantity
                        );
                    }

                    break;
                }
            }

            // Item exists in Load but not Invoice
            if (!found) {

                throw new RuntimeException(
                        "Item "
                                + loadItem.getItem().getItemsNames()
                                + " exists in Load but not in Invoice"
                );
            }
        }

        // Check InvoiceItems that are missing from Load
        for (InvoiceItem invoiceItem : invoiceItems) {

            boolean found = false;

            for (LoadItem loadItem : loadItems) {

                if (loadItem.getItem().getId()
                        .equals(invoiceItem.getItem().getId())) {

                    found = true;
                    break;
                }
            }

            if (!found) {

                throw new RuntimeException(
                        "Item "
                                + invoiceItem.getItem().getItemsNames()
                                + " exists in Invoice but not in Load"
                );
            }
        }
    }

    /*
     * PENDING → READY
     */
    public Load markReady(Long id) {

        Load load = getLoadById(id);

        if (load.getStatus() != LoadStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING load can be marked as READY"
            );
        }

        // Validate invoice and items before READY
        validateLoadItemsWithInvoice(load);

        load.setStatus(LoadStatus.READY);

        return loadRepository.save(load);
    }

    /*
     * READY → DISPATCHED
     */
    @Transactional
    public Load dispatchLoad(Long id) {

        Load load = getLoadById(id);

        if (load.getStatus() != LoadStatus.READY) {

            throw new RuntimeException(
                    "Load must be READY before dispatch"
            );
        }

        List<LoadItem> loadItems =
                loadItemRepository.findByLoad(load);

        if (loadItems.isEmpty()) {

            throw new RuntimeException(
                    "Cannot dispatch load without items"
            );
        }

        // Remove stock using FIFO
        for (LoadItem loadItem : loadItems) {

            StockMovement movement = new StockMovement();

            movement.setItem(loadItem.getItem());
            movement.setType(MovementType.OUT);
            movement.setQuantity(loadItem.getQuantity());

            // Stock movement does not directly require invoice
            movement.setInvoice(null);

            stockMovementService.insertStockMovement(movement);
        }

        load.setDispatchDate(LocalDateTime.now());
        load.setStatus(LoadStatus.DISPATCHED);

        return loadRepository.save(load);
    }

    /*
     * DISPATCHED → DELIVERED
     */
    public Load markDelivered(Long id) {

        Load load = getLoadById(id);

        if (load.getStatus() != LoadStatus.DISPATCHED) {

            throw new RuntimeException(
                    "Only DISPATCHED load can be marked as DELIVERED"
            );
        }

        load.setDeliveryDate(LocalDateTime.now());
        load.setStatus(LoadStatus.DELIVERED);

        return loadRepository.save(load);
    }
}