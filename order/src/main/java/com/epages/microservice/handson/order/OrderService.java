package com.epages.microservice.handson.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Order create(Order order);
    Order update(Order order);
    Optional<Order> getOrder(Long id);
    Page<Order> getAll(Pageable pageable);
    void setOrderStatus(Long id, OrderStatus status);
}
