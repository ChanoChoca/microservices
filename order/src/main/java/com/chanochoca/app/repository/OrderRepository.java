package com.chanochoca.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chanochoca.app.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
