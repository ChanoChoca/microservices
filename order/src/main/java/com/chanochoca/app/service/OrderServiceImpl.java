package com.chanochoca.app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chanochoca.app.domain.event.OrderCanceledEvent;
import com.chanochoca.app.domain.event.OrderPlacedEvent;
import com.chanochoca.app.model.Order;
import com.chanochoca.app.model.dto.OrderDTO;
import com.chanochoca.app.model.dto.OrderItemDTO;
import com.chanochoca.app.repository.OrderRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final InventoryClient inventoryClient;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(
            OrderRepository repository,
            InventoryClient inventoryClient,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.inventoryClient = inventoryClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Order> getAll() {
        logger.debug("Fetching all orders");
        return repository.findAll();
    }

    @Override
    @Transactional
    public String create(OrderDTO order) {
        logger.info("Creating order orderNumber={}", order.orderNumber());

        List<String> skuCodes = order.items().stream()
                .map(OrderItemDTO::skuCode)
                .toList();

        logger.debug("Checking inventory for skuCodes={}", skuCodes);

        if (!inventoryClient.areAllInStock(skuCodes)) {
            throw new RuntimeException("One or more products are out of stock");
        }

        Order savedOrder = repository.save(new Order(order));

        logger.info("Order persisted with id={} orderNumber={}",
            savedOrder.getId(), 
            savedOrder.getOrderNumber()
        );

        eventPublisher.publishEvent(
                new OrderPlacedEvent(savedOrder.getOrderNumber())
        );

        logger.info("OrderPlacedEvent published orderNumber={}",
            savedOrder.getOrderNumber()
        );

        return "Order placed";
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logger.warn("Deleting order id={}", id);
        Order order = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        repository.delete(order);

        eventPublisher.publishEvent(
                new OrderCanceledEvent(order.getOrderNumber())
        );
    }
}