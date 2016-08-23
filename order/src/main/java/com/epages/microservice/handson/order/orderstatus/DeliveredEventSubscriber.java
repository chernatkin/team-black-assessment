package com.epages.microservice.handson.order.orderstatus;

import com.epages.microservice.handson.order.OrderService;
import com.epages.microservice.handson.order.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveredEventSubscriber extends OrderStatusEventSubscriber {

    private static final String DELIVERED_EVENT_TYPE = "Delivered";

    @Autowired
    public DeliveredEventSubscriber(ObjectMapper objectMapper, OrderService orderService) {
        super(orderService, objectMapper, DELIVERED_EVENT_TYPE, OrderStatus.DELIVERED);
    }
}
