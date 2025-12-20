package com.chanochoca.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chanochoca.app.domain.event.OrderCanceledEvent;
import com.chanochoca.app.domain.event.OrderPlacedEvent;


@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void notifyOrderPlaced(OrderPlacedEvent event) {
        String message = "Su pedido " + event.orderNumber() + " fue confirmado.";

        sendEmail("cliente@example.com", message);
    }

    public void notifyOrderCanceled(OrderCanceledEvent event) {
        String message = "Su pedido " + event.orderNumber() + " fue cancelado.";

        sendEmail("cliente@example.com", message);
    }

    private void sendEmail(String recipient, String message) {
        logger.info("Email a {}: {}", recipient, message);
    }
}