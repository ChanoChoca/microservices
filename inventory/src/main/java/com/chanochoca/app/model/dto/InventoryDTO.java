package com.chanochoca.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record InventoryDTO(
    @Schema(example = "IPHONE-17")
    String skuCode,

    @Schema(example = "50")
    Long availableQuantity
) {}
