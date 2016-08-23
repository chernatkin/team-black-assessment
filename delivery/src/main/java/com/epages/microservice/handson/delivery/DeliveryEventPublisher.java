package com.epages.microservice.handson.delivery;

import com.epages.microservice.handson.delivery.order.Order;
import com.epages.microservice.handson.shared.event.EventPublisher;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeliveryEventPublisher {
    private static final String DELIVERY_ORDER_RECEIVED_EVENT_TYPE = "DeliveryOrderReceived";
    private static final String DELIVERED_EVENT_TYPE = "Delivered";

    private EventPublisher eventPublisher;

    @Autowired
    public DeliveryEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sendDeliveryOrderReceivedEvent(DeliveryOrderReceivedEvent event) {
        Map<String, Object> payloadMap = ImmutableMap.of(
                "orderLink", event.getOrderLink(),
                "estimatedTimeOfDelivery", event.getEstimatedTimeOfDelivery());
        eventPublisher.publish(DELIVERY_ORDER_RECEIVED_EVENT_TYPE, payloadMap);
    }

    public void sendDeliveredEvent(Order order) {
        Map<String, Object> payloadMap = ImmutableMap.of(
                "orderLink", order.getOrderLink());
        eventPublisher.publish(DELIVERED_EVENT_TYPE, payloadMap);
    }
}
