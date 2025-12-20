package com.chanochoca.app.service;

import java.util.List;

import com.chanochoca.app.model.Order;
import com.chanochoca.app.model.dto.OrderDTO;

public interface OrderService {
    List<Order> getAll();
    String create(OrderDTO order);
    void deleteById(Long id);
}
