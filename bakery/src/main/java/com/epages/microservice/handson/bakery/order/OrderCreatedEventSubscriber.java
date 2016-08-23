package com.epages.microservice.handson.bakery.order;

import com.epages.microservice.handson.bakery.BakeryService;
import com.epages.microservice.handson.shared.event.AbstractEventSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

@Component
public class OrderCreatedEventSubscriber extends AbstractEventSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedEventSubscriber.class);

    private static final String ORDER_CREATED_EVENT_TYPE = "OrderCreated";
    private BakeryService bakeryService;

    @Autowired
    public OrderCreatedEventSubscriber(ObjectMapper objectMapper, BakeryService bakeryService) {
        super(objectMapper, ORDER_CREATED_EVENT_TYPE);
        this.bakeryService = bakeryService;
        LOGGER.info("Created");
    }

    @Override
    protected void handleOwnType(Map<String, Object> event) {
        final Map<String, Object> payload = getPayload(event);
        String orderUriString = (String) payload.get("orderLink");
        URI orderUri = URI.create(orderUriString);
        bakeryService.acknowledgeOrder(orderUri);
        bakeryService.bakeOrder(orderUri);
        LOGGER.info("Consumed {} event with payload '{}' of class '{}'", ORDER_CREATED_EVENT_TYPE, payload, payload.getClass());
    }
}
