package com.chanochoca.app.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderDTO (
    @Schema(example = "ORD-2026-0001")
    String orderNumber,

    List<OrderItemDTO> items,

    @Schema(example = "3000")
    Long totalAmount,

    @Schema(example = "CREATED")
    Status status
) {
    
}
