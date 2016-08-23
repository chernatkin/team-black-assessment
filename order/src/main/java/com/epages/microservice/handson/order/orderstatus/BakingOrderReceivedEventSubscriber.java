package com.epages.microservice.handson.order.orderstatus;

import com.epages.microservice.handson.order.OrderService;
import com.epages.microservice.handson.order.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BakingOrderReceivedEventSubscriber extends OrderStatusEventSubscriber {

    private static final String BAKING_ORDER_RECEIVED_EVENT_TYPE = "BakingOrderReceived";

    @Autowired
    public BakingOrderReceivedEventSubscriber(OrderService orderService, ObjectMapper objectMapper) {
        super(orderService, objectMapper, BAKING_ORDER_RECEIVED_EVENT_TYPE, OrderStatus.BAKING);
    }
}
