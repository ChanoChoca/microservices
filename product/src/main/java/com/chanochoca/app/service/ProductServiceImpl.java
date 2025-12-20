package com.chanochoca.app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chanochoca.app.model.Product;
import com.chanochoca.app.model.dto.ProductDTO;
import com.chanochoca.app.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> getAll() {
        logger.debug("Fetching all products");
        return repository.findAll();
    }

    @Override
    public void create(ProductDTO product) {
        logger.info("Creating product sku={}",
                product.sku()
        );
        repository.save(new Product(product));
    }

    @Override
    public void deleteById(Long id) {
        logger.warn("Deleting product id={}", id);
        repository.deleteById(id);
    }
}
