package com.chanochoca.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chanochoca.app.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
