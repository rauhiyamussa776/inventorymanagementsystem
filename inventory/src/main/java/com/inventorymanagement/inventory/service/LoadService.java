package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Company;
import com.inventorymanagement.inventory.model.Load;
import com.inventorymanagement.inventory.model.LoadItem;
import com.inventorymanagement.inventory.model.LoadStatus;
import com.inventorymanagement.inventory.model.MovementType;
import com.inventorymanagement.inventory.model.StockMovement;
import com.inventorymanagement.inventory.repository.CampanyRepository;
import com.inventorymanagement.inventory.repository.LoadItemRepository;
import com.inventorymanagement.inventory.repository.LoadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoadService {

    private final LoadRepository loadRepository;
    private final CampanyRepository companyRepository;
    private final LoadItemRepository loadItemRepository;
    private final StockMovementService stockMovementService;

    public LoadService(
            LoadRepository loadRepository,
            CampanyRepository companyRepository,
            LoadItemRepository loadItemRepository,
            StockMovementService stockMovementService) {

        this.loadRepository = loadRepository;
        this.companyRepository = companyRepository;
        this.loadItemRepository = loadItemRepository;
        this.stockMovementService = stockMovementService;
    }

    public Load insertLoad(Load load) {

        Long companyId = load.getCompany().getId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        load.setCompany(company);

        if (load.getCreatedDate() == null) {
            load.setCreatedDate(LocalDateTime.now());
        }

        load.setStatus(LoadStatus.PENDING);

        return loadRepository.save(load);
    }

    public List<Load> getAllLoads() {
        return loadRepository.findAll();
    }

    public Load getLoadById(Long id) {
        return loadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Load not found"));
    }

    public Load markReady(Long id) {

        Load load = getLoadById(id);

        if (load.getStatus() != LoadStatus.PENDING) {
            throw new RuntimeException(
                    "Only PENDING load can be marked as READY"
            );
        }

        load.setStatus(LoadStatus.READY);

        return loadRepository.save(load);
    }

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

        // Remove stock using FIFO for every item in the load
        for (LoadItem loadItem : loadItems) {

            StockMovement movement = new StockMovement();

            movement.setItem(loadItem.getItem());
            movement.setType(MovementType.OUT);
            movement.setQuantity(loadItem.getQuantity());
            movement.setInvoice(null);

            stockMovementService.insertStockMovement(movement);
        }

        load.setDispatchDate(LocalDateTime.now());
        load.setStatus(LoadStatus.DISPATCHED);

        return loadRepository.save(load);
    }
    
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