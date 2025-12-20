package com.chanochoca.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chanochoca.app.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
