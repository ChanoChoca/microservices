package com.chanochoca.app.domain.event;

public sealed interface OrderEvent permits OrderPlacedEvent, OrderCanceledEvent {
    String orderNumber();
}
