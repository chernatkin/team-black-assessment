package com.epages.microservice.handson.order;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final PizzaClientService pizzaClientService;
    private final OrderEventPublisher orderEventPublisher;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            PizzaClientService pizzaClientService,
                            OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.pizzaClientService = pizzaClientService;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public Order create(Order order) {
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("order does not have items");
        }
        getLineItemPrices(order);
        order.setOrderedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.saveAndFlush(order);

        orderEventPublisher.sendOrderCreatedEvent(savedOrder);
        LOGGER.info("order created {}", order);
        return savedOrder;
    }

    private void getLineItemPrices(Order order) {
        order.getItems().forEach(lineItem -> {
            Pizza pizza = pizzaClientService.getPizza(lineItem.getPizza());
            lineItem.setPrice(pizza.getPrice());
        });
    }

    @Override
    public Order update(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        return Optional.ofNullable(orderRepository.findOne(id));
    }

    @Override
    public Page<Order> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public void setOrderStatus(Long id, OrderStatus status) {
        Order order = getOrder(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order %s not found", id)));
        order.setStatus(status);
        update(order);
    }
}
