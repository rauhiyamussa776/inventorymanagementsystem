package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.model.Load;
import com.inventorymanagement.inventory.service.LoadService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loads")
public class LoadController {

    private final LoadService loadService;

    public LoadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @PostMapping
    public Load insertLoad(@RequestBody Load load) {
        return loadService.insertLoad(load);
    }

    @GetMapping
    public List<Load> getAllLoads() {
        return loadService.getAllLoads();
    }

    @GetMapping("/{id}")
    public Load getLoadById(@PathVariable Long id) {
        return loadService.getLoadById(id);
    }
    @PutMapping("/{id}/ready")
public Load markReady(@PathVariable Long id) {
    return loadService.markReady(id);
}

@PutMapping("/{id}/dispatch")
public Load dispatchLoad(@PathVariable Long id) {
    return loadService.dispatchLoad(id);
}
@PutMapping("/{id}/delivered")
public Load markDelivered(@PathVariable Long id) {
    return loadService.markDelivered(id);
}
}