package com.epages.microservice.handson.order.orderstatus;

import com.epages.microservice.handson.order.OrderService;
import com.epages.microservice.handson.order.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BakingFinishedEventSubscriber extends OrderStatusEventSubscriber {

    private static final String BAKING_FINISHED_EVENT_TYPE = "BakingFinished";

    @Autowired
    public BakingFinishedEventSubscriber(ObjectMapper objectMapper, OrderService orderService) {
        super(orderService, objectMapper, BAKING_FINISHED_EVENT_TYPE, OrderStatus.READY_FOR_DELIVERY);
    }
}
