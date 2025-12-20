package com.chanochoca.app.domain.event;

public record OrderCanceledEvent(String orderNumber) implements OrderEvent {
    
}
