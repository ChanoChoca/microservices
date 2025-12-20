package com.chanochoca.app.model.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderItemDTO(
    @Schema(example = "IPHONE-17")
    String skuCode,

    @Schema(example = "1500")
    BigDecimal price,

    @Schema(example = "2")
    Integer quantity
) {
    
}
