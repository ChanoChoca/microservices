package com.chanochoca.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chanochoca.app.model.Inventory;
import com.chanochoca.app.model.dto.InventoryDTO;
import com.chanochoca.app.model.dto.InventoryResponse;
import com.chanochoca.app.service.InventoryService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/inventories")
public class InventoryController {
    private final InventoryService service;
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> getAll() {
        logger.info("GET /inventories");
        return service.getAll();
    }

    @GetMapping("/in-stock")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        logger.info("GET /inventories/in-stock skuCodes={}", skuCode);
        return service.isInStock(skuCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody InventoryDTO inventory) {
        logger.info("POST /inventories sku={} qty={}",
            inventory.skuCode(),
            inventory.availableQuantity()
        );
        service.create(inventory);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.info("DELETE /inventories id={}", id);
        service.deleteById(id);
    }
}
