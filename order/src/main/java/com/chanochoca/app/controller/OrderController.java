package com.chanochoca.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chanochoca.app.model.Order;
import com.chanochoca.app.model.dto.OrderDTO;
import com.chanochoca.app.service.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {
    private final OrderService service;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getAll() {
        logger.info("GET /orders");
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public Mono<String> create(@RequestBody OrderDTO order) {
        logger.info("POST /orders orderNumber={}", order.orderNumber());
        return Mono.fromCallable(() -> service.create(order));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        logger.warn("DELETE /orders id={}", id);
        service.deleteById(id);
    }

    public Mono<String> fallbackMethod(Order order, Throwable ex) {
        logger.error(
            "Inventory service failure for orderNumber={}",
            order.getOrderNumber(),
            ex
        );
        return Mono.just("Oops! Something went wrong, please order after some time!");
    }
}
