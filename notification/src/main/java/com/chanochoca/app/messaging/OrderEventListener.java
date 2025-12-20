package com.chanochoca.app.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.chanochoca.app.domain.event.OrderCanceledEvent;
import com.chanochoca.app.domain.event.OrderEvent;
import com.chanochoca.app.domain.event.OrderPlacedEvent;
import com.chanochoca.app.service.NotificationService;

@Component
public class OrderEventListener {
    private final NotificationService service;

    public OrderEventListener(NotificationService service) {
        this.service = service;
    }

    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void handle(OrderEvent event) {
        
        if (event instanceof OrderPlacedEvent placed) {
            service.notifyOrderPlaced(placed);
            return;
        }

        if (event instanceof OrderCanceledEvent canceled) {
            service.notifyOrderCanceled(canceled);
            return;
        }
    }
}
