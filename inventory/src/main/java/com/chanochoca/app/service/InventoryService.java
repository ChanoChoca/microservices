package com.chanochoca.app.service;

import java.util.List;

import com.chanochoca.app.model.Inventory;
import com.chanochoca.app.model.dto.InventoryDTO;
import com.chanochoca.app.model.dto.InventoryResponse;

public interface InventoryService {
    List<Inventory> getAll();
    List<InventoryResponse> isInStock(List<String> skuCode);
    void create(InventoryDTO inventory);
    void deleteById(Long id);
    void reduceStock(Long quantity);
}
