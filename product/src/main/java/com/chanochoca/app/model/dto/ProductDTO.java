package com.chanochoca.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductDTO(
    @Schema(example = "iPhone 17")
    String name, 
    @Schema(example = "Smartphone Apple")
    String description,
    @Schema(example = "1500.00")
    Double price,
    @Schema(example = "IPHONE-17")
    String sku, 
    @Schema(example = "Apple")
    String brand, 
    @Schema(example = "50")
    Long stock, 
    @Schema(example = "CREATED")
    Status status) {
}
