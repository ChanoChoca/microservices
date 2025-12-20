package com.chanochoca.app.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chanochoca.app.model.Inventory;
import com.chanochoca.app.model.dto.InventoryDTO;
import com.chanochoca.app.model.dto.InventoryResponse;
import com.chanochoca.app.repository.InventoryRepository;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Inventory> getAll() {
        logger.debug("Fetching all inventory records");
        return repository.findAll();
    }

    @Override
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        logger.info("Checking stock for skuCodes={}", skuCode);

        List<InventoryResponse> response = repository.findBySkuCodeIn(skuCode).stream()
               .map(inventory -> {
                    boolean inStock = inventory.getAvailableQuantity() > 0;
                    logger.debug("skuCode={} inStock={}", inventory.getSkuCode(), inStock);
 
                    InventoryResponse r = new InventoryResponse();
                    r.setSkuCode(inventory.getSkuCode());
                    r.setInStock(inStock);
                    return r;
                }).toList();

        logger.info("Stock check completed for {} skuCodes", skuCode.size());
        return response;
    }

    @Override
    public void create(InventoryDTO inventory) {
        Inventory inventorySaved = repository.save(new Inventory(inventory));
        logger.info("Created inventory id={} qty={}",
            inventorySaved.getId(),
            inventorySaved.getAvailableQuantity()
        );
    }

    @Override
    public void deleteById(Long id) {
        logger.warn("Deleting inventory id={}", id);    
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void reduceStock(Long quantity) {
        List<Inventory> inventories = repository.findAll();
        if (inventories.isEmpty()) throw new RuntimeException("No inventory available");

        Inventory inventory = inventories.get(0);
        Long currentQty = inventory.getAvailableQuantity();
        if (currentQty < quantity) throw new RuntimeException("Insufficient stock");

        inventory.setAvailableQuantity(currentQty - quantity);
        inventory.setUpdatedAt(LocalDate.now());
        repository.save(inventory);
    }
}