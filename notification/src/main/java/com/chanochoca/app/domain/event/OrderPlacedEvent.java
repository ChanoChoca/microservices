package com.chanochoca.app.domain.event;

public record OrderPlacedEvent(String orderNumber) implements OrderEvent {
    
}
