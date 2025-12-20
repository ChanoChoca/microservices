package com.chanochoca.app.service;

import java.util.List;

import com.chanochoca.app.model.Product;
import com.chanochoca.app.model.dto.ProductDTO;

public interface ProductService {
    List<Product> getAll();
    void create(ProductDTO product);
    void deleteById(Long id);
}
