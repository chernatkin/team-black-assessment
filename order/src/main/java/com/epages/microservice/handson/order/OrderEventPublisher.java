package com.epages.microservice.handson.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.stereotype.Component;

import com.epages.microservice.handson.shared.event.EventPublisher;
import com.google.common.collect.ImmutableMap;

@Component
public class OrderEventPublisher {

    private final EventPublisher eventPublisher;
    private final EntityLinks entityLinks;

    private static final String ORDER_CREATED_EVENT_TYPE = "OrderCreated";

    @Autowired
    public  OrderEventPublisher(EventPublisher eventPublisher, EntityLinks entityLinks) {
        this.eventPublisher = eventPublisher;
        this.entityLinks = entityLinks;
    }

    public void sendOrderCreatedEvent(final Order order) {
        Map<String, Object> payloadMap = ImmutableMap.of(
                "orderLink", entityLinks.linkForSingleResource(Order.class, order.getId()).toUri().toString());
        eventPublisher.publish(ORDER_CREATED_EVENT_TYPE, payloadMap);
    }

}
